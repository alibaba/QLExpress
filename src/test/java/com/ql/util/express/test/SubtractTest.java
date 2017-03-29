package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

public class SubtractTest {
	@Test
	public void testMax() throws Exception {
		//String express = "return max(max(0.0,1) - 0.95,0);";
		String express = "-3-(-5*-7-9)-(9-2);";
		ExpressRunner runner = new ExpressRunner(false,true);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		Object r = runner.execute(express, context, null, false, true);
		System.out.println(r);
		Assert.assertTrue("\"-\"号测试",r.toString().equals("-36"));
	}
}
