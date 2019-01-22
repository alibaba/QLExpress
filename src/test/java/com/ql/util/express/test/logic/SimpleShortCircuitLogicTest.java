package com.ql.util.express.test.logic;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 短路逻辑测试类
 * @author tianqiao
 *
 */
public class SimpleShortCircuitLogicTest {
	
	private ExpressRunner runner = new ExpressRunner();
	
	public void initial() throws Exception{
		runner.getOperatorFactory().getOperator("<").setErrorInfo("$1 < $2 = false");
        runner.getOperatorFactory().getOperator(">").setErrorInfo("$1 > $2 = false");
	}
	
	public boolean calculateLogicTest(String expression,IExpressContext<String,Object> expressContext,List<String> errorInfo) throws Exception {			
        Boolean result = (Boolean)runner.execute(expression, expressContext, errorInfo, true, false);
        if(result.booleanValue() == true){
        	return true;
        }
        return false;
	}	
	
	/**
	 * 测试非短路逻辑,并且输出出错信息
	 * @throws Exception
	 */
	@Test
	public void testShortCircuit() throws Exception {
		runner.setShortCircuit(true);
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();	
		expressContext.put("违规天数", 100);
		expressContext.put("虚假交易扣分", 11);
		expressContext.put("VIP", false);
		List<String> errorInfo = new ArrayList<String>();
		initial();
		String expression ="( 2 < 1 ) and (违规天数 < 90 or 虚假交易扣分 < 12)";
		boolean result = calculateLogicTest(expression, expressContext, errorInfo);
		if(result){
			System.out.println(expression+" is true!");
		}else{
			System.out.println(expression+" is false!");
			for(String error : errorInfo){
				System.out.println(error);
			}
		}
		
	}
	
	/**
	 * 测试非短路逻辑,并且输出出错信息
	 * @throws Exception
	 */
	@Test
	public void testNoShortCircuit() throws Exception {
		runner.setShortCircuit(false);
		IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();	
		expressContext.put("违规天数", 100);
		expressContext.put("虚假交易扣分", 11);
		expressContext.put("VIP", false);
		List<String> errorInfo = new ArrayList<String>();
		initial();
		String expression ="( 2 < 1 ) and (违规天数 < 90 or 虚假交易扣分 < 12)";
		boolean result = calculateLogicTest(expression, expressContext, errorInfo);
        if(result){
            System.out.println(expression+" is true!");
        }else{
            System.out.println(expression+" is false!");
			for(String error : errorInfo){
				System.out.println(error);
			}
		}
		
	}

}
