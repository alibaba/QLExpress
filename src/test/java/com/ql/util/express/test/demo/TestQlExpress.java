package com.ql.util.express.test.demo;

import java.util.HashMap;
import java.util.Map;

import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

@SpringApplicationContext("classpath:spring-express-config.xml")
public class TestQlExpress  extends UnitilsJUnit4{
	
	@SpringBeanByName
	QlExpressUtil qlExpressUtil;
	
	
	/**
	 * 使用qlExpressUtil扩展了QlExpressRunner的expressContext参数，
	 * 使脚本中可以直接调用spring中的bean
	 * 
	 * 场景业务逻辑如下：
	 * ******************************************************************
	 * 
	 * 用户qlExpress注册一个账号
	 * 用户qlExpress开了个淘宝店
     * 通过自己的苦心经营，星级不断升高，qlExpress不断的期望着能够地店铺升级为商城用户
     * 终于有一天他成功了。
     * 
	 * ******************************************************************
	 * @throws Exception
	 */
	@org.junit.Test
	public void testScript() throws Exception{

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("nick", "qlExpress");
		qlExpressUtil.execute("用户A = bizLogicBean.signUser(nick);" +
							  "bizLogicBean.openShop(用户A );" +
				              "for(;; bizLogicBean.isShopOpening(用户A ) && !bizLogicBean.upgradeShop(用户A )){bizLogicBean.addScore(用户A );}", 
				              context);
	}
	
	
	/**
	 * 
	 * 使用预先定义的函数，脚本即逻辑：
	 * ******************************************************************
	 * 
	 * "用户A = 注册用户(nick);" +
	 * "开店(用户A);" +
     * "for(;;店铺营业中(用户A) && !店铺升级(用户A)){星级自增(用户A);}
     *
     * ******************************************************************
	 * @throws Exception
	 */
	
	@org.junit.Test
	public void testDeclareMethodScript() throws Exception{

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("nick", "qlExpress");
		qlExpressUtil.execute("用户A = 注册用户(nick);" +
							  "开店(用户A);" +
				              "for(;;店铺营业中(用户A) && !店铺升级(用户A)){星级自增(用户A);}", 
				              context);
	}
	

}
