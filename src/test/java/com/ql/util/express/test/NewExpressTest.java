package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;


public class NewExpressTest {
	@Test
	public void testParse() throws Exception{
		String[][] expresses = new String[][]{
				{"0 - 3295837566L","-3295837566"},
				{"1==1? 50+50:100+100","100"},
				{"1==2? 50+50:100+100","200"},				
				{"int[][] abc = new int[10][10]; abc[0][0] = 100; abc[0][0]-10","90"},
				{"Integer.parseInt(\"1\")-1","0"},
				{"Double.parseDouble(\"-0.22\")","-0.22"},
				{"(int)((Double.parseDouble(\"0.22\")-0.21)*100)","1"},
				{"1+2+/** 注释测试 **/ 12+1 ","16"},
				{"3240732988055L","3240732988055"},
				{"3240732988054","3240732988054"},
				{"0.5d","0.5"},
				{"0.3f","0.3"},
				{"0.55","0.55"},
				{"1+1","2"},
				{"(1+1)*(9-7);","4"},
				{"1+1;2+5","7"},
				{"false && true","false"},
				{"true || fale","true"},				
				{"return 100/2;","50"},
				{"return 10;1 + 1;","10"},
				{"if(1==1) then{ return 100; }else{return 10;}","100"},
				{"if(1==2) then{ return 100; }else{return 10;}","10"},
				{"if(1==1) then{ return 100;}","100"},
				{"if(1==2) then{ return 100;}","null"},				
				{"if(1==1) { return 100; }else{return 10;}","100"},
				{"if(1==2) { return 100; }else{return 10;}","10"},
				{"if(1==1) { return 100;}","100"},
				{"if(1==2) { return 100;}","null"},
				{"int i = 2","2"},
				{"i=2;i<10;","true"},
				{"a  =0 ; for(int i=0;i<10;i=i+1){a = a + 1;} return a;","10"},
				{"new String(\"ss\")","ss"},
				{"(new String[1][1])[0][0]","null"},
				{"a = new String[1][9];  a[0][1+1] = \"qianghui\"; b = a[0][2]; ","qianghui"},
				{"(new String[3][5])[1].length","5"},
				{"\"abc\".length()","3"},
				{"\"abc\".substring(1,3).substring(1,2)","c"},
				{"Integer.SIZE","32"},
				{"new com.ql.util.express.test.BeanExample(\"qianghui\").name","qianghui"},
				{"System.out.println(1)","null"},
				{"int a = 0;for(int i= 0;i<10;i++){ System.out.println(i); a= a+ i} return a;","45"},
				{"int a = 0;for(int i= 0;i<10;i++){ if(i > 5) then{break;}  a= a+ i;} return a;","15"},
				{"int a = 0;for(int i= 0;i<10;i++){ if(i <=5) then{continue;}  a= a+ i;} return a;","30"},
				{"int a =0; alias pa a; pa++ ;return a","1"},
				{"int[][] a = new int[10][10]; alias pa a[0]; pa[0] =100 ;return a[0][0]","100"},
				{"int[][] a = new int[10][10]; {exportAlias pa a[0]; pa[0] =100} ;pa[1] =200; return a[0][0] + a[0][1];","300"},
				{"int[][] a = new int[10][10]; {exportDef int i = 1; exportDef int j=1; a[i][j] =999} ; return a[i][j];","999"},
				{"return ((float)9)/2","4.5"},
				{"int a =9; return ((float)a)/2","4.5"},
				{"float a =9; return a/2","4.5"},
				{"macro  惩罚    {100 + 100} 惩罚;","200"},
				{"function union(String a,String b){return a +'-'+ b;}; union(\"qiang\",\"hui\")","qiang-hui"},
				{" 3+4 in (8+3,7,9)","true"},
				{"\"ab\" like \"a%\"","true"},
				{"int 中国 = 100; int 美国 = 200 ;return 中国 + 美国","300"},
				{"1 加 1 ","2"},
				{" 'a' love 'b' love 'c'","c{b{a}b}c"},
				{"if 1==2 then {return 10}else{return 100}","100"}
		};
		for(int i=0;i<expresses.length;i++){
			IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
			ExpressRunner runner = new ExpressRunner(false,true);
			runner.addOperatorWithAlias("加", "+",null);
			runner.addOperator("love","+",new LoveOperator("love"));
			Object result = runner.execute(expresses[i][0],expressContext, null, false,true);
			System.out.println("运算结果：" + result);
			System.out.println("环境结果：" + expressContext);		
			Assert.assertTrue("表达式执行错误:" + expresses[i][0] + " 期望值：" + expresses[i][1] +" 运算结果：" + result ,expresses[i][1].equals(result == null?"null":result.toString()));
		}
	}
}
