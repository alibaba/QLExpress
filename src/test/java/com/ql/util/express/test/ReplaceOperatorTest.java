package com.ql.util.express.test;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import org.junit.Assert;
import org.junit.Test;

public class ReplaceOperatorTest {
    @Test
    public void testReplaceOperatorTest() throws Exception {
        String express = " 3 + 4";
        ExpressRunner runner = new ExpressRunner();
        Object r = runner.execute(express, null, null, false, false);
        System.out.println(r);
        Assert.assertTrue("表达式计算", r.toString().equalsIgnoreCase("7"));
        runner.replaceOperator("+", new ReplaceOperatorAddReduce("+"));
        r = runner.execute(express, null, null, false, false);
        System.out.println(r);
        runner.replaceOperator("+", new ReplaceOperatorAddReduce2("+"));
        r = runner.execute(express, null, null, false, false);
        System.out.println(r);
        runner.replaceOperator("+", new ReplaceOperatorAddReduce("+"));
        r = runner.execute(express, null, null, false, false);
        System.out.println(r);
        Assert.assertTrue("替换操作符号错误", r.toString().equalsIgnoreCase("(3*4)"));
    }
}

class ReplaceOperatorAddReduce extends Operator {
    public ReplaceOperatorAddReduce(String name) {
        this.name = name;
    }

    public ReplaceOperatorAddReduce(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1,
        Object op2) {
        return "(" + op1 + "*" + op2 + ")";
    }
}

class ReplaceOperatorAddReduce2 extends Operator {
    public ReplaceOperatorAddReduce2(String name) {
        this.name = name;
    }

    public ReplaceOperatorAddReduce2(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1,
        Object op2) {
        return "(" + op1 + "****" + op2 + ")";
    }
}
