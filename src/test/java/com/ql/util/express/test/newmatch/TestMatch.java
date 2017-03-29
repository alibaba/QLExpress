package com.ql.util.express.test.newmatch;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.test.ExpressContextExample;

public class TestMatch {
	@org.junit.Test
	public void testExpress() throws Exception{
		ExpressRunner runner = new ExpressRunner(false,true);
		String[][] expressTest = new String[][] {
				{"1+2","3"},	
				{"2-1","1"},	
				{"2+(-1)","1"},	
				{"(3+3)*(4+4)/3 + 8 - 2*2","20"},
				{"7==8","false"},
				{"!((8>=8) && !false) || 9==9","true"},
				{"a=(b=9)","9"},
				{"int a = 1+3","4"},	
				{"(int[][] a = new int[2][2]).length","2"},	
				{" f = (exportDef int b = (int a = 2+3))","5"},	
				{"name=new String() + 100","100"},	
				{"name=new String((3+3)*4+\"$\") + 100","24$100"},	
				{"name=new String(\"xuannan-\") + 100","xuannan-100"},	
				{"new String[2][10].length","2"},	
				{"[1,2,3,4,5].length","5"},	
				{"[1,2,3,4,5][2]","3"},	
				{"[[1,2],[3,4]].length","2"},	
				{"1000 + new String(\"abc\").substring(1).substring(1).length() + 100","1101"},
				{"new Object().getClass().getAnnotations().length","0"},
				{"new Object().getClass().getMethods()[1].getName()","wait"},
//				{"Object.class.getName().getClass().getMethods().length","72"},
				{"1/2","0"},
				{"1/(double)2","0.5"},
				{"(double)1/2","0.5"},
				{"((Object)\"ABC\").getClass().getName()","java.lang.String"},
				{"[[1,2],[3,4]][0][1]","2"},
				{"(new String[2])[1]","null"},
				{"max(2,100)","100"},
				{"max(max(1,2,3),min(2,100))","3"},
				{"true?1:2","1"},
				{"9==9?100+200:10+20","300"},
				{"9==8?100+200:20+20","40"},
				{"return a=100+90","190"},
				{"alias xuannan qianghui","null"},
				{"max 2-1,3,4+2","6"},
				{"int a = 2 + 1;b=100;return a + b","103"},
				{"{int a = 10;{int a = 20;} return a;}","10"},
				{"{int a = 10;{a = 20;} return a;}","20"},
				{"if true then return 100 else return 200","100"},
				{"if true then {return 100;} else {return 200;}","100"},
				{"if (false) then return 100 else return 200","200"},
				{"if (1!=1) then {return 100;} else {return 200;}","200"},
				{"if true then return 100","100"},
				{"if true then {return 100;} ","100"},
				{"if (false) then return 100","null"},
				{"if (1!=1) then {return 100;}","null"},
				{"if (true) return 100; else return 200","100"},
				{"if (true) {return 100;} else {return 200;}","100"},
				{"if (false) return 100; else return 200","200"},
				{"if (1!=1) {return 100;} else {return 200;}","200"},
				{"if (false) if(false) return 100;else return 200; else return 300;","300"},
				{"int a = 0;{a = a + 100;}{ a= a+ 200;}","300"},
				{"int a =0;for(int i=1;i<=10;i++){a = a + i;} return a;","55"},
				{"int a =0;for(int i=1;i<10;i++){if(i >5) break; a = a + 100;} return a;","500"},
				{"function abc(){return 100;} return abc()","100"},
				{"function abc(int a,int b){ return a + b;} return abc(1+100,2*100)","301"},
				{"macro abc { return a + 100;} int a = 100; return abc + 100","300"},
				{"class Person(String aName,int aYear){" +
						"String name = aName;" +
						"int year = aYear;" +
						"function getName(){return name;}" +
						"function getYear(){return year;}" +
						"} " +
						"Person person =new Person(\"xuannan\",100);" +
						"return person.getName() + '-' + person.getYear();" 
						,"xuannan-100"},
				{"map = NewMap('ABC':100,'BCD':200,'DEF':1000 + 1000);return map.get('BCD')","200"},
				{"Object[] abc = [];return abc.length","0"},
				{"2 in 2","true"},
				{"2 in (4,5,6)","false"},
				{"(-1)","-1"},
				{"/**1,2,3,4**/1+2","3"},
				{"1+/**1,2,3,4**/2+3","6"},
				{"(new String[3][5])[1].length","5"},
		        {"class ABC(com.ql.util.express.test.BeanExample bean,String name){"
					+"InnerClass a = new InnerClass();"
					+ "哈希值:{bean.hashCode();};"
					+ "class InnerClass(){" +
							"int 计数 =200;" +
							"};"
					+ "}" +
							"return new ABC(new com.ql.util.express.test.BeanExample(),'xuannan').a.计数" ,
				  "200" 
				},
				{";i=100;;","100"}
				};
		for (int point = 0; point < expressTest.length; point++) {
			String expressStr = expressTest[point][0];
			List<String> errorList = new ArrayList<String>();
			IExpressContext<String,Object> expressContext = new ExpressContextExample(null);		
			Object result = runner.execute(expressStr,expressContext, null, false,false);
			if (expressTest[point][1].equalsIgnoreCase("null")
					&& result != null
					|| expressTest[point][1].equalsIgnoreCase(result==null?"null":result.toString()) == false) {
				throw new Exception("处理错误,计算结果与预期的不匹配:" + expressStr + " = " + result + "但是期望值是：" + expressTest[point][1]);
			}
			System.out.println("Example " + point + " : " + expressStr + " =  " + result);
			System.out.println(expressContext);
			if(errorList.size() > 0){
			   System.out.println("\t\t系统输出的错误提示信息:" + errorList);
			}
		}
	 }
}
