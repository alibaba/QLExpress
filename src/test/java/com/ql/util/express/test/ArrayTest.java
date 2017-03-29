package com.ql.util.express.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

public class ArrayTest {

	@Test
	public void testABC() throws Exception {
		String express = "int[][] abc = new int[2][2];"
				+ " int[] b = new int[2]; " + "abc[0] = b;" + " b[0] =11;"
				+ " abc[0][1] = 22; " + "return abc;";
		ExpressRunner runner = new ExpressRunner();
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		int[][] r = (int[][]) runner.execute(express, context, null, false,
				true);
		System.out.println(r[0][1]);
		Assert.assertTrue("数组操作实现错误", r[0][1] == 22);
	}

	@Test
	public void testAnonyNewArrayOrMapOrList() throws Exception {
		ExpressRunner runner = new ExpressRunner(false, false);
		String[][] expressTest = new String[][] {
				{"int[] abc = [1,2,3];return abc[2]","3"},
				{"int[][] abc = [[11,12,13],[21,22,23]];return abc[1][2]","23"},
				{"String[] abc = [\"xuannan\",\"qianghui\"];return abc[1]","qianghui"},
				{"String[] abc = [\"xuannan\"+100,\"qianghui\"+100];return abc[1]","qianghui100"},
				{"Object[] abc = [];return abc.length","0"},
				{"Map abc = NewMap(1:1,2:2); return abc.get(1) + abc.get(2)","3"},
				{"Map abc = NewMap(\"a\":1,\"b\":2); return abc.a + abc.b","3"},
				{"int o1 =10; int o2=20;String k1 =\"a\";String k2 =\"b\";  Map abc = NewMap(k1:o1,k2:o2); return abc.a + abc.b","30"},				
				{"Map abc = NewMap(1:\"xuannan\",2:\"qianghui\"); return abc.get(1) +\"-\"+ abc.get(2)","xuannan-qianghui"},
				{"List abc = NewList(1,2,3); return abc.get(1)","2"},
				};
		IExpressContext<String, Object> expressContext = new ExpressContextExample(
				null);

		for (int point = 0; point < expressTest.length; point++) {
			String expressStr = expressTest[point][0];
			List<String> errorList = new ArrayList<String>();
			Object result = runner.execute(expressStr, expressContext, null,
					false, false);
			if (result == null
					&& expressTest[point][1].equalsIgnoreCase("null") == false
					|| expressTest[point][1].equalsIgnoreCase("null")
					&& result != null
					|| result != null
					&& expressTest[point][1]
							.equalsIgnoreCase(result.toString()) == false) {
				throw new Exception("处理错误,计算结果与预期的不匹配:" + expressStr + " = "
						+ result + "但是期望值是：" + expressTest[point][1]);
			}
			System.out.println("Example " + point + " : " + expressStr + " =  "
					+ result);
			if (errorList.size() > 0) {
				System.out.println("\t\t系统输出的错误提示信息:" + errorList);
			}
		}
		System.out.println(expressContext);
	}
}
