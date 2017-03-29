package com.ql.util.express.example;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

public class BeanTest {

	@Test
	public void test1() throws Exception{
		String exp = "import com.ql.util.express.example.CustBean;" + 
		        "CustBean cust = new CustBean(1);" +
		        "cust.setName(\"小强\");" +
		        "return cust.getName();";
		ExpressRunner runner = new ExpressRunner();
		//执行表达式，并将结果赋给r
		String r = (String)runner.execute(exp,null,null,false,false);
		System.out.println(r);
		Assert.assertTrue("操作符执行错误","小强".equals(r));
	}
	
	@Test
	public void test2() throws Exception{
		String exp = "cust.setName(\"小强\");" +
			     // "cust.name = \"小强\";" + 
		        "return cust.getName();";
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
		expressContext.put("cust", new CustBean(1));
		ExpressRunner runner = new ExpressRunner();
		//执行表达式，并将结果赋给r
		String r = (String)runner.execute(exp,expressContext,null,false,false);
		System.out.println(r);
		Assert.assertTrue("操作符执行错误","小强".equals(r));
	}
	
	@Test
	public void test3() throws Exception{
		String exp = "首字母大写(\"abcd\")";
		ExpressRunner runner = new ExpressRunner();
		runner.addFunctionOfClassMethod("首字母大写", CustBean.class.getName(), "firstToUpper", new String[]{"String"},null);
		//执行表达式，并将结果赋给r
		String r = (String)runner.execute(exp,null,null,false,false);
		System.out.println(r);
		Assert.assertTrue("操作符执行错误","Abcd".equals(r));
	}
	
	/**
	 * 使用别名
	 * @throws Exception
	 */
	@Test
	public void testAlias() throws Exception{
		String exp = "cust.setName(\"小强\");" +
			      "定义别名 custName cust.name;" + 
		        "return custName;";
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
		expressContext.put("cust", new CustBean(1));
		ExpressRunner runner = new ExpressRunner();
		//
		runner.addOperatorWithAlias("定义别名", "alias", null);
		//执行表达式，并将结果赋给r
		String r = (String)runner.execute(exp,expressContext,null,false,false);
		System.out.println(r);
		Assert.assertTrue("操作符执行错误","小强".equals(r));
	}
	
	/**
	 * 使用宏
	 * @throws Exception
	 */
	@Test
	public void testMacro() throws Exception{
		String exp = "cust.setName(\"小强\");" +
			      "定义宏 custName {cust.name};" + 
		        "return custName;";
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
		expressContext.put("cust", new CustBean(1));
		ExpressRunner runner = new ExpressRunner();
		//
		runner.addOperatorWithAlias("定义宏", "macro", null);
		//执行表达式，并将结果赋给r
		String r = (String)runner.execute(exp,expressContext,null,false,false);
		System.out.println(r);
		Assert.assertTrue("操作符执行错误","小强".equals(r));
	}
}
