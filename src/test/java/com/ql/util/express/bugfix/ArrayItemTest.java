package com.ql.util.express.bugfix;

import com.ql.util.express.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayItemTest {

    @Test
    public void issue37() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);

        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        String express = "part = \"1@2@3\".split(\"@\");\n" +
                "Integer.valueOf(part[2]);";
        Object res = runner.execute(express, context, null, true, true);
        assertEquals(3, res);
    }

    @Test
    public void issue43() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        String exp = "System.out.println(args[0]);";
        String[] args = {"123","456"};
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("args", args);
        runner.execute(exp,context,null,false,true);
    }

}
