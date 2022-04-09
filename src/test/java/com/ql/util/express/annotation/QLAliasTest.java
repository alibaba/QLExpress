package com.ql.util.express.annotation;

import com.alibaba.qlexpress4.cache.CacheFactory;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author tianqiao@come-future.com
 * 2021-11-15 5:51 下午
 */
public class QLAliasTest {
    @Test
    public void testQLAlias() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        String[] exps = new String[] {
//            "患者.birth", "1987-02-23",
//            "患者.生日()", "1987-02-23",
//            "患者.患者姓名", "老王",
//            "患者.姓名", "老王",
//            "患者.getBirth()==患者.出生年月()", "true",//方法注解
//            "患者.生日()==患者.生日", "true",//get方法和字段名字一样是不冲突的
//            "患者.患者姓名 + ' 今年 '+ 患者.获取年龄() +' 岁'", "老王 今年 34 岁",//任意方法的注解
            "患者.级别='低风险';return 患者.级别;", "低风险",
        };
        //1、测试对象的方法，字段上的别名标签
        IExpressContext<String, Object> context = new DefaultContext<>();
        Person person = new Patient();
        person.setName("老王");
        person.setSex("男");
        person.setBirth("1987-02-23");
        context.put("患者", person);
        for (int i = 0; i < exps.length; i += 2) {
            Object result = runner.execute(exps[i], context, null, false, false);
            System.out.println(result);
            assertEquals(("" + result), exps[i + 1]);
        }

        //2、外部业务调用方也可以利用类、参数的QLAlias注解，自动注入ExpressContext
        QLAliasContext context2 = new QLAliasContext();
        context2.putAutoParams(person);//等价于context2.put("患者", person);
        for (int i = 0; i < exps.length; i += 2) {
            Object result = runner.execute(exps[i], context2, null, false, false);
            System.out.println(result);
            assertEquals(("" + result), exps[i + 1]);
        }
    }
}
