package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;


public class ForFlowFunctionTest {

	@Test
	public void testABC() throws Exception {
		String express = "for(i=0;i<1;i=i+1){" + "打印(70)"
				+ "}打印(70); return 10";
		ExpressRunner runner = new ExpressRunner(false,true);
		runner.addFunctionOfServiceMethod("打印", System.out, "println",
				new String[] { "int" }, null);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		Object r = runner.execute(express, context, null, false, true);
		Assert.assertTrue("for循环后面跟着一个函数的时候错误", r.toString().equals("10"));
	}

}
