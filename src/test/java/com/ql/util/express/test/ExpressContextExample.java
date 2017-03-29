package com.ql.util.express.test;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationContext;

import com.ql.util.express.IExpressContext;

@SuppressWarnings("serial")
public class ExpressContextExample extends HashMap<String,Object> implements IExpressContext<String,Object>{
	
	private ApplicationContext context;

    public ExpressContextExample(ApplicationContext aContext){
    	this.context = aContext;
    }
    public ExpressContextExample( Map<String,Object> aProperties,ApplicationContext aContext){
    	super(aProperties);
    	this.context = aContext;
    }
    
	/**
	 * 抽象方法：根据名称从属性列表中提取属性值
	 */
	public Object get(Object name) {
		Object result = null;
		if(((String)name).equalsIgnoreCase("三星卖家")){
			result = Boolean.valueOf(true);
		} else if (((String)name).equalsIgnoreCase("消保用户")) {
			result = Boolean.valueOf(true);
		} else {
			result = super.get(name);
		}
		try{
		if (result == null &&this.context!= null && this.context.containsBean((String)name)) {
			//如果在Spring容器中包含bean，则返回String的Bean
			result = this.context.getBean((String)name);
		}
		}catch(Exception  e){
		  throw new RuntimeException(e);	
		}
		return result;
	}

	public Object  put(String name, Object object) {
	   if(name.equalsIgnoreCase("myDbData")){
		   throw new RuntimeException("没有实现");
	   }
       return  super.put(name,object);
	}


}
