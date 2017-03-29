package com.ql.util.express.example;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.example.operator.AddNOperator;
import com.ql.util.express.example.operator.AddTwiceOperator;

/**
 * 本例用于展示如何自定义操作符和方法
 *
 */
public class OperatorTest {

	/**
	 * 定义一个简单的二元操作符
	 * @throws Exception
	 */
	@Test
	public void testAddTwice() throws Exception{
		//定义表达式，相当于 1+(22+22)+(2+2)
		String exp = " 1 addT 22 addT 2";
		ExpressRunner runner = new ExpressRunner();
		//定义操作符addT，其实现为AddTwiceOperator
		runner.addOperator("addT", new AddTwiceOperator());
		//执行表达式，并将结果赋给r
		int r = (Integer)runner.execute(exp,null,null,false,false);
		System.out.println(r);
		Assert.assertTrue("操作符执行错误",r==49);
	}

	/**
	 * 定义一个多元操作符
	 * @throws Exception
	 */
	@Test
	public void testAddNByOperator() throws Exception{
		//定义表达式，相当于4+1+2+3
		String exp = "4 addN (1,2,3)";
		ExpressRunner runner = new ExpressRunner();
		//定义操作符addN，其实现为AddNOperator，语法格式与in一致
		runner.addOperator("addN","in",new AddNOperator());
		//执行表达式，并将结果赋给r
		int r = (Integer)runner.execute(exp,null,null,false,false);
		System.out.println(r);
		Assert.assertTrue("操作符执行错误",r==10);
	}
	
	/**
	 * 定义一个方法
	 * @throws Exception
	 */
	@Test
	public void testAddNByFunction() throws Exception{
		//定义表达式，相当于1+2+3+4
		String exp = "addN(1,2,3,4)";
		ExpressRunner runner = new ExpressRunner();
		//定义方法addN，其实现为AddNOperator
		runner.addFunction("addN",new AddNOperator());
		//执行表达式，并将结果赋给r
		int r = (Integer)runner.execute(exp,null,null,false,false);
		System.out.println(r);
		Assert.assertTrue("操作符执行错误",r==10);
	}
	
	/**
	 * 使用参数
	 * @throws Exception
	 */
	@Test
	public void testAddTwiceWithParams() throws Exception{
		//定义表达式，相当于 i+(j+j)+(n+n)
		String exp = " i addT j addT n";
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
		expressContext.put("i", Integer.valueOf(1));
		expressContext.put("j", Integer.valueOf(22));
		expressContext.put("n", Integer.valueOf(2));
		ExpressRunner runner = new ExpressRunner();
		//定义操作符addT，其实现为AddTwiceOperator
		runner.addOperator("addT", new AddTwiceOperator());
		//执行表达式，并将结果赋给r
		int r = (Integer)runner.execute(exp,expressContext,null,false,false);
		System.out.println(r);
		Assert.assertTrue("操作符执行错误",r==49);
	}
}
