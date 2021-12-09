package com.ql.util.express.test;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import org.junit.Test;

public class DemoShow {

    /**
     * 四则运算
     *
     * @throws Exception
     */
    @Test
    public void testArithmetic() throws Exception {
        ExpressRunner runner = new ExpressRunner(true, true);
        runner.execute("(1+2)*3", null, null, false, true);
    }

    /**
     * for循环
     *
     * @throws Exception
     */
    @Test
    public void testForLoop() throws Exception {
        ExpressRunner runner = new ExpressRunner(true, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        runner.execute("sum=0;for(i=0;i<10;i=i+1){sum=sum+i;}", context, null,
            true, true);
    }

    /**
     * for嵌套循环
     *
     * @throws Exception
     */
    @Test
    public void testForLoop2() throws Exception {
        ExpressRunner runner = new ExpressRunner(true, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        runner.execute(
            "sum=0;for(i=0;i<10;i=i+1){for(j=0;j<10;j++){sum=sum+i+j;}}",
            context, null, false, true);
    }

    /**
     * 汉诺塔算法
     *
     * @throws Exception
     */
    @Test
    public void testHanoiMethod() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        runner.addFunctionOfClassMethod("汉诺塔算法", DemoShow.class.getName(),
            "hanoi", new Class[] {int.class, char.class, char.class,
                char.class}, null);
        runner.execute("汉诺塔算法(3, '1', '2', '3')", null, null, false, false);
    }

    /**
     * 汉诺塔算法2
     *
     * @throws Exception
     */
    @Test
    public void testHanoiMethod2() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        runner.addFunctionOfServiceMethod("汉诺塔算法", new DemoShow(), "hanoi",
            new Class[] {int.class, char.class, char.class, char.class},
            null);
        runner.execute("汉诺塔算法(3, '1', '2', '3')", null, null, false, false);
    }

    /**
     * 汉诺塔算法3
     *
     * @throws Exception
     */
    @Test
    public void testHanoiMethod3() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addFunctionOfServiceMethod("汉诺塔算法", new DemoShow(), "hanoi",
            new Class[] {int.class, char.class, char.class, char.class},
            null);
        runner.addMacro("汉诺塔算法演示", "汉诺塔算法(3, '1', '2', '3')");
        runner.execute("汉诺塔算法演示", null, null, false, false);
    }

    // 将n个盘从one座借助two座,移到three座
    public void hanoi(int n, char one, char two, char three) {
        if (n == 1) {move(one, three);} else {
            hanoi(n - 1, one, three, two);
            move(one, three);
            hanoi(n - 1, two, one, three);
        }
    }

    private void move(char x, char y) {
        System.out.println(x + "--->" + y);
    }

    /**
     * 自定义操作符
     *
     * @throws Exception
     */
    @Test
    public void testOperator() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        DefaultContext<String, Object> context = new DefaultContext<>();
        runner.addOperator("join", new JoinOperator());
        Object r = runner.execute("1 join 2 join 3", context, null, false, false);
        System.out.println(r);
    }

    @SuppressWarnings({"unchecked", "rawtypes", "serial"})
    public class JoinOperator extends Operator {
        public Object executeInner(Object[] list) throws Exception {
            Object opdata1 = list[0];
            Object opdata2 = list[1];
            if (opdata1 instanceof List) {
                ((List)opdata1).add(opdata2);
                return opdata1;
            } else {
                List result = new ArrayList();
                result.add(opdata1);
                result.add(opdata2);
                return result;
            }
        }
    }

    /**
     * 替换操作符
     *
     * @throws Exception
     */
    @Test
    public void testReplaceOperator() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = runner.execute("1 + 2 + 3", context, null, false, false);
        System.out.println(r);
        runner.replaceOperator("+", new JoinOperator());
        r = runner.execute("1 + 2 + 3", context, null, false, false);
        System.out.println(r);
    }

    /**
     * 替换操作符
     *
     * @throws Exception
     */
    @Test
    public void testShortLogicAndErrorInfo() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("A类违规天数90天内", true);
        context.put("虚假交易扣分", 11);
        context.put("假冒扣分", 11);
        context.put("待整改卖家", false);
        context.put("宝贝相符DSR", 4.0);
        String expression =
            "A类违规天数90天内 ==false and (虚假交易扣分<48 or 假冒扣分<12) and 待整改卖家 ==false and 宝贝相符DSR>4.6";
        expression = initial(runner, expression);
        List<String> errorInfo = new ArrayList<>();
        boolean result = (Boolean)runner.execute(expression, context, errorInfo, true, false);
        if (result) {
            System.out.println("符合营销活动规则");
        } else {
            System.out.println("不符合营销活动规则");
            for (String error : errorInfo) {
                System.out.println(error);
            }
        }
    }

    public String initial(ExpressRunner runner, String expression) throws Exception {
        runner.setShortCircuit(false);
        runner.addOperatorWithAlias("小于", "<", "$1 < $2 不符合");
        runner.addOperatorWithAlias("大于", ">", "$1 > $2 不符合");
        runner.addOperatorWithAlias("等于", "==", "$1 == $2 不符合");
        return expression.replaceAll("<", " 小于 ").replaceAll(">", " 大于 ").replaceAll("==", " 等于 ");
    }

    /**
     * 预加载表达式 & 虚拟类
     *
     * @throws Exception
     */
    @Test
    public void testVirtualClass() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.loadMutilExpress("类初始化", "class People(){sex;height;money;skin};");
        runner.loadMutilExpress("创建小强", "a = new People();a.sex='male';a.height=185;a.money=10000000;");
        runner.loadMutilExpress("体检", "if(a.sex=='male' && a.height>180 && a.money>5000000) return '高富帅，鉴定完毕'");
        DefaultContext<String, Object> context = new DefaultContext<>();

        Object r = runner.execute("类初始化;创建小强;体检", context, null, false, false);
        System.out.println(r);
    }
}
