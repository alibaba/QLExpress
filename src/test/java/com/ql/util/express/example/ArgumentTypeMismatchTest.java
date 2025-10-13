package com.ql.util.express.example;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.Operator;
import com.ql.util.express.instruction.op.OperatorBase;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

/**
 * Created by tianqiao on 17/6/4.
 */
public class ArgumentTypeMismatchTest {

    private static final ArgumentTypeMismatchTest singleton = new ArgumentTypeMismatchTest();

    public void functionABC(Long a, Integer b, String c) {
        System.out.println("functionABC");
    }

    @Test
    public void test1() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addFunctionOfServiceMethod("abc", singleton, "functionABC",
            new Class[] {Long.class, Integer.class, String.class}, null);
        String exp = "abc(a,b,c)";
        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("a", 1L);
        context.put("b", 2);
        context.put("c", "3");
        runner.execute(exp, context, null, false, false);
    }

    @Test
    public void test2() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addFunction("abc", new Operator() {
            @Override
            public Object executeInner(Object[] list) {
                Long paramA = Long.valueOf(list[0].toString());
                Integer paramB = Integer.valueOf(list[1].toString());
                String paramC = list[2].toString();
                singleton.functionABC(paramA, paramB, paramC);
                return null;
            }
        });
        String exp = "abc(a,b,c)";
        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("a", "1");
        context.put("b", "2");
        context.put("c", "3");
        runner.execute(exp, context, null, false, false);
    }

    @Test
    public void test3() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addFunction("abc", new Operator() {
            @Override
            public Object executeInner(Object[] list) {
                Long paramA = Long.valueOf(list[0].toString());
                Integer paramB = Integer.valueOf(list[1].toString());
                String paramC = list[2].toString();
                singleton.functionABC(paramA, paramB, paramC);
                return null;
            }
        });

        OperatorBase function = runner.getFunction("abc");
        System.out.println(
            "function = " + ToStringBuilder.reflectionToString(function, ToStringStyle.MULTI_LINE_STYLE));

        String exp = "abc(a,b,c)";
        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("a", "1");
        context.put("b", "2");
        context.put("c", "3");

        InstructionSet instructionSet = runner.getInstructionSetFromLocalCache(exp);
        String[] outFunctionNames = runner.getOutFunctionNames(exp);
        String[] outVarNames = runner.getOutVarNames(exp);
        System.out.println("before execute instructionSet = " + instructionSet);
        System.out.println("outFunctionNames = " + ToStringBuilder.reflectionToString(outFunctionNames,
            ToStringStyle.MULTI_LINE_STYLE));
        System.out.println(
            "outVarNames = " + ToStringBuilder.reflectionToString(outVarNames, ToStringStyle.MULTI_LINE_STYLE));

        runner.execute(exp, context, null, false, false);

        instructionSet = runner.getInstructionSetFromLocalCache(exp);
        outFunctionNames = runner.getOutFunctionNames(exp);
        outVarNames = runner.getOutVarNames(exp);
        System.out.println("after execute instructionSet = " + instructionSet);
        System.out.println("outFunctionNames = " + ToStringBuilder.reflectionToString(outFunctionNames,
            ToStringStyle.MULTI_LINE_STYLE));
        System.out.println(
            "outVarNames = " + ToStringBuilder.reflectionToString(outVarNames, ToStringStyle.MULTI_LINE_STYLE));
    }
}
