package com.ql.util.express.example;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.example.operator.ApproveOperator;
import org.junit.Test;

/**
 * 本例模拟了一个简单的流程处理
 * 用于展示如何定义表达式，方法，并使用上下文变量
 */
public class WorkflowTest {
    public void print(String s) {
        System.out.print(s);
    }

    public void println(String s) {
        System.out.println(s);
    }

    /**
     * 执行一段文本
     *
     * @throws Exception
     */
    @Test
    public void testApprove1() throws Exception {
        // 此脚本内容与example/approve.ql及example/approve1.ql中的一段脚本内容完全相同
        String express = ""
            + "如果 (审批通过(经理, 金额)) {\n"
            + "    如果 (金额 大于 5000) {\n"
            + "        如果 (审批通过(总监, 金额)) {\n"
            + "            如果 (审批通过(财务, 金额)) {\n"
            + "                报销入账(金额)\n"
            + "            } 否则 {\n"
            + "                打回修改(申请人)\n"
            + "            }\n"
            + "        } 否则 {\n"
            + "            打回修改(申请人)\n"
            + "        }\n"
            + "    } 否则 {\n"
            + "        如果 (审批通过(财务, 金额)) {\n"
            + "            报销入账(金额)\n"
            + "        } 否则 {\n"
            + "            打回修改(申请人)\n"
            + "        }\n"
            + "    }\n"
            + "} 否则 {\n"
            + "    打回修改(申请人)\n"
            + "}\n"
            + "打印(\"完成\")\n";
        System.out.println("express = " + express);
        ExpressRunner runner = new ExpressRunner();

        //定义操作符别名
        runner.addOperatorWithAlias("如果", "if", null);
        runner.addOperatorWithAlias("否则", "else", null);
        runner.addOperatorWithAlias("大于", ">", null);
        runner.addFunctionOfServiceMethod("打印", new WorkflowTest(), "println", new String[] {"String"}, null);

        //定义方法
        runner.addFunction("审批通过", new ApproveOperator(1));
        runner.addFunction("报销入账", new ApproveOperator(2));
        runner.addFunction("打回修改", new ApproveOperator(3));

        //设置上下文变量
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        expressContext.put("经理", "王经理");
        expressContext.put("总监", "李总监");
        expressContext.put("财务", "张财务");
        expressContext.put("申请人", "小强");
        expressContext.put("金额", 4000);

        runner.execute(express, expressContext, null, false, false);
    }

    /**
     * 通过文件加载表达式
     *
     * @throws Exception
     */
    @Test
    public void testApprove2() throws Exception {
        ExpressRunner runner = new ExpressRunner();

        //定义操作符别名
        runner.addOperatorWithAlias("如果", "if", null);
        runner.addOperatorWithAlias("否则", "else", null);
        runner.addOperatorWithAlias("大于", ">", null);
        runner.addFunctionOfServiceMethod("打印", new WorkflowTest(), "println", new String[] {"String"}, null);

        //定义方法
        runner.addFunction("审批通过", new ApproveOperator(1));
        runner.addFunction("报销入账", new ApproveOperator(2));
        runner.addFunction("打回修改", new ApproveOperator(3));

        //加载文件，从指定文件中获取表示式构造指令集
        runner.loadExpress("example/approve1");

        //设置上下文变量
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        expressContext.put("经理", "王经理");
        expressContext.put("总监", "李总监");
        expressContext.put("财务", "张财务");
        expressContext.put("申请人", "小强");
        expressContext.put("金额", 5000);

        runner.executeByExpressName("example/approve1", expressContext, null, false, false, null);
    }

    /**
     * 通过文件加载方法及表达式
     *
     * @throws Exception
     */
    @Test
    public void testApprove3() throws Exception {
        ExpressRunner runner = new ExpressRunner();

        //定义操作符别名
        runner.addOperatorWithAlias("如果", "if", null);
        runner.addOperatorWithAlias("否则", "else", null);
        runner.addOperatorWithAlias("大于", ">", null);
        runner.addFunctionOfServiceMethod("打印", new WorkflowTest(), "println", new String[] {"String"}, null);

        //加载文件
        runner.loadExpress("example/approve");

        //设置上下文变量
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        expressContext.put("经理", "王经理");
        expressContext.put("总监", "李总监");
        expressContext.put("财务", "张财务");
        expressContext.put("申请人", "小强");
        expressContext.put("金额", 6000);

        runner.executeByExpressName("example/approve", expressContext, null, false, false, null);
    }

    /**
     * 从不同的文件中加载方法及表达式
     *
     * @throws Exception
     */
    @Test
    public void testApprove4() throws Exception {
        ExpressRunner runner = new ExpressRunner();

        //定义操作符别名
        runner.addOperatorWithAlias("如果", "if", null);
        runner.addOperatorWithAlias("否则", "else", null);
        runner.addOperatorWithAlias("大于", ">", null);
        runner.addFunctionOfServiceMethod("打印", new WorkflowTest(), "println", new String[] {"String"}, null);

        //加载文件
        runner.loadExpress("example/approve1");
        runner.loadExpress("example/approve2");

        //设置上下文变量
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        expressContext.put("经理", "王经理");
        expressContext.put("总监", "李总监");
        expressContext.put("财务", "张财务");
        expressContext.put("申请人", "小强");
        expressContext.put("金额", 7000);

        runner.executeByExpressName("example/approve1", expressContext, null, false, false, null);
    }
}
