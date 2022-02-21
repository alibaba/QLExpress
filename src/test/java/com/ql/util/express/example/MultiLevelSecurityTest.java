package com.ql.util.express.example;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.config.whitelist.CheckerFactory;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.exception.QLSecurityRiskException;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class MultiLevelSecurityTest {

    // 黑白名单测试
    @Test
    public void blockWhiteListControlTest() throws Exception {
        // 黑名单测试
        try {
            QLExpressRunStrategy.setForbidInvokeSecurityRiskMethods(true);
            // 这里不区分静态方法与成员方法, 写法一致
            // 不支持重载, riskMethod 的所有重载方法都会被禁止
            QLExpressRunStrategy.addSecurityRiskMethod(RiskBean.class, "riskMethod");
            ExpressRunner expressRunner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<>();
            try {
                expressRunner.execute("import com.ql.util.express.example.RiskBean;" +
                        "RiskBean.riskMethod()", context, null, true, false);
                fail("没有捕获到不安全的方法");
            } catch (Exception e) {
                assertTrue(e.getCause() instanceof QLSecurityRiskException);
            }

            // 白名单测试
            // 有白名单设置时, 则黑名单失效
            QLExpressRunStrategy.addSecureMethod(RiskBean.class, "secureMethod");
            // 白名单中的方法, 允许正常调用
            expressRunner.execute("import com.ql.util.express.example.RiskBean;" +
                    "RiskBean.secureMethod()", context, null, true, false);
            try {
                // java.lang.String.length 不在白名单中, 不允许调用
                expressRunner.execute("'abcd'.length()", context,
                        null, true, false);
                fail("没有捕获到不安全的方法");
            } catch (Exception e) {
                assertTrue(e.getCause() instanceof QLSecurityRiskException);
            }

            // setSecureMethod 设置方式
            Set<String> secureMethods = new HashSet<>();
            secureMethods.add("java.lang.String.length");
            secureMethods.add("java.lang.Integer.valueOf");
            QLExpressRunStrategy.setSecureMethods(secureMethods);
            // 白名单中的方法, 允许正常调用
            Object res = expressRunner.execute("Integer.valueOf('abcd'.length())", context,
                    null, true, false);
            assertEquals(4, res);
            try {
                // java.lang.Long.valueOf 不在白名单中, 不允许调用
                expressRunner.execute("Long.valueOf('abcd'.length())", context,
                        null, true, false);
                fail("没有捕获到不安全的方法");
            } catch (Exception e) {
                assertTrue(e.getCause() instanceof QLSecurityRiskException);
            }
        } finally {
            QLExpressRunStrategy.setForbidInvokeSecurityRiskMethods(false);
        }
    }

    @Test
    public void compileWhiteListTest() throws Exception {
        try {
            // 设置编译期白名单
            QLExpressRunStrategy.setCompileWhiteCheckerList(Arrays.asList(
                    // 精确设置
                    CheckerFactory.must(Date.class),
                    // 子类设置
                    CheckerFactory.assignable(List.class)
            ));
            ExpressRunner expressRunner = new ExpressRunner();
            // Date 在编译期白名单中, 可以显示引用
            expressRunner.execute("new Date()", new DefaultContext<>(), null,
                    false, true);
            // LinkedList 是 List 的子类, 符合白名单要求
            expressRunner.execute("LinkedList ll = new LinkedList; ll.add(1); ll.add(2); ll",
                    new DefaultContext<>(), null, false, true);
            try {
                // String 不在白名单中, 不可以显示引用
                // 但是隐式引用, a = 'mmm', 或者定义字符串常量 'mmm' 都是可以的
                expressRunner.execute("String a = 'mmm'", new DefaultContext<>(), null,
                        false, true);
            } catch (Exception e) {
                assertTrue(e.getCause() instanceof QLSecurityRiskException);
            }

            // Math 不在白名单中
            // 对于不满足编译期类型白名单的脚本无需运行, 即可通过 checkSyntax 检测出
            assertFalse(expressRunner.checkSyntax("Math.abs(-1)"));
        } finally {
            QLExpressRunStrategy.setCompileWhiteCheckerList(null);
        }
    }

    @Test
    public void sandboxModeTest() throws Exception {
        try {
            QLExpressRunStrategy.setSandBoxMode(true);
            ExpressRunner expressRunner = new ExpressRunner();
            // 沙箱模式下不支持 import 语句
            assertFalse(expressRunner.checkSyntax("import com.ql.util.express.example.RiskBean;"));
            // 沙箱模式下不支持显式的类型引用
            assertFalse(expressRunner.checkSyntax("String a = 'abc'"));
            assertTrue(expressRunner.checkSyntax("a = 'abc'"));
            // 无法用 . 获取 Java 类属性或者 Java 类方法
            try {
                expressRunner.execute("'abc'.length()", new DefaultContext<>(),
                        null, false, true);
                fail();
            } catch (QLException e) {
                // 没有找到方法:length
            }
            try {
                DefaultContext<String, Object> context = new DefaultContext<>();
                context.put("test", new CustBean(12));
                expressRunner.execute("test.id", context,
                        null, false, true);
                fail();
            } catch (RuntimeException e) {
                // 无法获取属性:id
            }

            // 沙箱模式下可以使用 自定义操作符/宏/函数 和应用进行交互
            expressRunner.addFunction("add", new Operator() {
                @Override
                public Object executeInner(Object[] list) throws Exception {
                    return (Integer) list[0] + (Integer) list[1];
                }
            });
            assertEquals(3, expressRunner.execute("add(1,2)", new DefaultContext<>(),
                    null, false, true));
            // 可以用 . 获取 map 的属性
            DefaultContext<String, Object> context = new DefaultContext<>();
            HashMap<Object, Object> testMap = new HashMap<>();
            testMap.put("a", "t");
            context.put("test", testMap);
            assertEquals("t", expressRunner.execute("test.a", context,
                    null, false, true));
        } finally {
            QLExpressRunStrategy.setSandBoxMode(false);
        }
    }
}
