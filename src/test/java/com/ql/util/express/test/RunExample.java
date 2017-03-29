package com.ql.util.express.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;
import com.ql.util.express.instruction.OperateDataCacheManager;

public class RunExample implements ApplicationContextAware, Runnable {
	private ApplicationContext applicationContext;
	ExpressRunner runner;
	String[][] expressTest1 = new String[][]{
			{ "( 2  属于 (4,3,5)) or isVIP(\"qhlhl2010@gmail.com\") or  isVIP(\"qhlhl2010@gmail.com\")", "false" },
	};
	String[][] expressTest = new String[][] {
			{ "isVIP(\"qh\") ; isVIP(\"xuannan\"); return isVIP(\"qh\") ;", "false" },				
			{ "如果  三星卖家  则  'a' love 'b'  否则   'b' love 'd' ", "b{a}b" },
			{"int defVar = 100; defVar = defVar + 100;", "200"},
			{"int a=0; if false then a = 5 else  a=10+1 ; return a ","11"},
			{ " 3+ (1==2?4:3) +8","14"},
			{ " 如果  (true) 则 {2+2;} 否则 {20 + 20;} ","4"},
			{"'AAAAAAA' +'-' + \"\" +'' + \"B\"","AAAAAAA-B"},
			//{ "System.out.println(\"ss\")", "null" },
			{"unionName = new com.ql.util.express.test.BeanExample(\"张三\").unionName(\"李四\")",
					"张三-李四" }, 
					{ "group(2,3,4)", "9" },
					{ "取绝对值(-5.0)", "5.0" },
					{ "取绝对值TWO(-10.0)", "10.0" },
			{ "max(2,3,4,10)", "10" },
			{"max(2,-1)","2"},
			{ "max(3,2) + 转换为大写(\"abc\")", "3ABC" },
			{ "c = 1000 + 2000", "3000" },
			{ "b = 累加(1,2,3)+累加(4,5,6)", "21" },
			{ "三星卖家 and 消保用户 ", "true" },
			{ "new String(\"12345\").length()" ,"5"},
			{ "'a' love 'b' love 'c' love 'd'", "d{c{b{a}b}c}d" },
			{ "10 * (10 + 1) + 2 * (3 + 5) * 2", "142" },
			{ "( 2  属于 (4,3,5)) or isVIP(\"qhlhl2010@gmail.com\") or  isVIP(\"qhlhl2010@gmail.com\")", "false" },
			{" 1!=1 and isVIP(\"qhlhl2010@gmail.com\")","false"},
			{" 1==1 or isVIP(\"qhlhl2010@gmail.com\") ","true"},
			{ "abc == 1", "true" },
			{ "2+2 in 2+2", "true" },
			{ "true or null", "true" },
			{ "null or true", "true" },
			{ "null or null", "false" },
			
			{ "true and null", "false" },
			{ "null and true", "false" },
			{ "null and null", "false" },
			
			{ "'a' nor null", "a" },
			{ "'a' nor 'b'", "a" },
			{ " null nor null", "null" },
			{ " null nor 'b'", "b" },
			
		//	{ "testLong(abc)", "toString-long:1" },
			{ "bean.testLongObject(abc)", "toString-LongObject:1" },
			
			{"sum=0;n=7.3;for(i=0;i<n;i=i+1){sum=sum+i;};sum;","28"},
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
	public RunExample()throws Exception{
		runner = new ExpressRunner();
		initialRunner(runner);
	}
	public RunExample(ExpressRunner runner) {
		this.runner = runner;
	}
	public void setApplicationContext(ApplicationContext context) {
		this.applicationContext = context;
	}
    public static void initialRunner(ExpressRunner runner) throws Exception{
		runner.addOperatorWithAlias("如果", "if",null);
		runner.addOperatorWithAlias("则", "then",null);
		runner.addOperatorWithAlias("否则", "else",null);

		runner.addOperator("love", new LoveOperator("love"));
		runner.addOperatorWithAlias("属于", "in", "用户$1不在允许的范围");
		runner.addOperatorWithAlias("myand", "and", "用户$1不在允许的范围");
		runner.addFunction("累加", new GroupOperator("累加"));
		runner.addFunction("group", new GroupOperator("group"));
		runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(),
				"isVIP", new String[] { "String" }, "$1不是VIP用户");
		runner.addFunctionOfClassMethod("取绝对值", Math.class.getName(), "abs",
				new String[] { "double" }, null);
		runner.addFunctionOfClassMethod("取绝对值TWO", Math.class.getName(), "abs",
				new Class[] { double.class }, null);
		runner.addFunctionOfClassMethod("转换为大写", BeanExample.class.getName(),
				"upper", new String[] { "String" }, null);		
		runner.addFunctionOfClassMethod("testLong", BeanExample.class.getName(),
				"testLong", new String[] { "long" }, null);		

		IExpressContext<String,Object> expressContext = new ExpressContextExample(null);
		expressContext.put("b", new Integer(200));
		expressContext.put("c", new Integer(300));
		expressContext.put("d", new Integer(400));
		expressContext.put("bean", new BeanExample());
		expressContext.put("abc",1l);
		expressContext.put("defVar",1000);
		
    }
	public static void main(String[] args) throws Exception {
    	ExpressRunner runner = new ExpressRunner(false,true);
    	initialRunner(runner);
		new RunExample(runner).run(1); //111
		//new RunExample(runner).run(100000); // 8466
		
		for (int i = 0; i < 5; i++) {
			new Thread(new RunExample(runner)).start();
		}
//		 System.out.println(OperateDataCacheManager.getFetchCount());
	}
	public void run() {
		run(1000000000);
	}
	public void run(int num) {
		long start = System.currentTimeMillis();
		try {
			for (int j = 0; j < num; j++) {
				IExpressContext<String,Object> expressContext = new ExpressContextExample(	this.applicationContext);
				expressContext.put("a", j);
				expressContext.put("b", new Integer(200));
				expressContext.put("c", new Integer(300));
				expressContext.put("d", new Integer(400));
				expressContext.put("bean", new BeanExample());
				for (int point = 0; point < expressTest.length; point++) {
					String s = expressTest[point][0];
					// 批量处理的时候可以先预处理，会加快执行效率
					//List<String> errorList = new ArrayList<String>();
					// Object result = runner.parseInstructionSet(s);
					 runner.execute(s, expressContext, null, true, false);
//					if (expressTest[point][1].equalsIgnoreCase("null")
//							&& result != null
//							|| result != null
//							&& expressTest[point][1].equalsIgnoreCase(result							
//									.toString()) == false) {
//						throw new Exception("处理错误,计算结果与预期的不匹配");
//					}
//					System.out.println(s + " 执行结果 ： " + result);
//					System.out.println("错误信息" + errorList);
				}
			//	System.out.println(expressContext);
			 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread() + "耗时："
				+ (System.currentTimeMillis() - start));
	}
	
	public void run2(int num) {
		long start = System.currentTimeMillis();
		try {
			for (int j = 0; j < num; j++) {
				String[][] expressTest = new String[][] {
//						{ "System.out.println(\"ss\")", "null" },
						{"unionName = new com.ql.util.express.test.BeanExample(\"张三\").unionName(\"李四\")",
								"张三-李四" }, 
						{ "max(2,3,4,10)", "10" },
						{ " max(3,2) + 转换为大写(\"abc\")", "3ABC" },
						{ " null == null", "true" },
						{ " c = 1000 + 2000", "3000" },
						{ "b = 累加(1,2,3)+累加(4,5,6)", "21" },
						{ "三星卖家 and 消保用户 ", "true" },
						{ " ((1 +  1) 属于 (4,3,5)) and isVIP(\"qhlhl2010@gmail.com\")", "false" },
						{ "group(2,3,4)", "9" }, { "取绝对值(-5)", "5.0" },
						{ "2 属于(3,4)", "false" },
						{ "true myand false", "false" },
						{ "'a' love 'b' love 'c' love 'd'", "d{c{b{a}b}c}d" },
						{ " 10 * 10 + 1 + 2 * 3 + 5 * 2", "117" },
						{" 1!=1 and 2==2 and 1 == 2","false"},
						{" 80 > \"300\"","true"}
						};
				IExpressContext<String,Object> expressContext = new ExpressContextExample(	this.applicationContext);
				expressContext.put("a", j);
				expressContext.put("b", new Integer(200));
				expressContext.put("c", new Integer(300));
				expressContext.put("d", new Integer(400));
				expressContext.put("bean", new BeanExample());
				for (int point = 0; point < expressTest.length; point++) {
					String s = expressTest[point][0];
					// 批量处理的时候可以先预处理，会加快执行效率
					//List<String> errorList = new ArrayList<String>();
					 Object result = runner.execute(s,expressContext, null, false,false);
					if (expressTest[point][1].equalsIgnoreCase("null")
							&& result != null
							|| result != null
							&& expressTest[point][1].equalsIgnoreCase(result							
									.toString()) == false) {
						throw new Exception("处理错误,计算结果与预期的不匹配");
					}
//					System.out.println(s + " 执行结果 ： " + result);
//					System.out.println("错误信息" + errorList);
				}
			//	System.out.println(expressContext);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread() + "耗时："
				+ (System.currentTimeMillis() - start));
	}
		
}
class EqualIn extends Operator{

	@Override
	public Object executeInner(Object[] list) throws Exception {
		for (int i = 0 ; i < list.length ; i ++){
			System.out.println(list[i]);
		}
		return Boolean.TRUE;
	}
	
}

