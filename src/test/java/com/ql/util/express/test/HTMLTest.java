package com.ql.util.express.test;

import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

public class HTMLTest {
    @Test
    public void testABC() throws Exception {
        //String express ="\"<div style=\\\"font-family:宋体;font-size:12px;line-height:25px;\\\">经费收入（\"";
        ExpressRunner runner = new ExpressRunner(false, true);
        String express = "\"经\\\"费收\\\"入\\\"aaa-\" + 100";
        Object r = runner.execute(express, null, null, false, true);
        System.out.println(r);
        System.out.println("经\"费收\"入\"aaa-100");
        Assert.assertEquals("字符串解析错误：", "经\"费收\"入\"aaa-100", r);
    }
}
