package com.ql.util.express;

import java.util.HashMap;
import java.util.Map;

import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.OperateDataCacheManager;

public class InstructionSetContext  implements IExpressContext<String,Object> {
	/*
	 * 没有知道数据类型的变量定义是否传递到最外层的Context
	 */
	private boolean isExpandToParent = true;
	
	private IExpressContext<String,Object> parent = null;
	private Map<String,Object> content;
	/**
	 * 符号表
	 */
	private Map<String,Object> symbolTable =new HashMap<String,Object>();
	
	private ExpressLoader expressLoader;
	
	private boolean isSupportDynamicFieldName = false;

	public ExpressRunner getRunner() {
		return runner;
	}

	private ExpressRunner runner;
	
	public InstructionSetContext(boolean aIsExpandToParent,ExpressRunner aRunner,IExpressContext<String,Object> aParent,ExpressLoader aExpressLoader,boolean aIsSupportDynamicFieldName){
	   this.initial(aIsExpandToParent, aRunner, aParent, aExpressLoader, aIsSupportDynamicFieldName);
	}
	public void initial(boolean aIsExpandToParent,ExpressRunner aRunner,IExpressContext<String,Object> aParent,ExpressLoader aExpressLoader,boolean aIsSupportDynamicFieldName){
	    this.isExpandToParent = aIsExpandToParent;
		this.runner = aRunner;
		this.parent = aParent;
		this.expressLoader = aExpressLoader;
		this.isSupportDynamicFieldName = aIsSupportDynamicFieldName;		
	}
    public void clear(){
    	isExpandToParent = true;    	
    	parent = null;
    	content = null;
    	expressLoader = null;    	
    	isSupportDynamicFieldName = false;
    	runner = null;   
    	symbolTable.clear();
    	
    }
	public void exportSymbol(String varName,Object aliasNameObject) throws Exception{
		if( this.parent != null && this.parent instanceof InstructionSetContext){
			((InstructionSetContext)this.parent).exportSymbol(varName, aliasNameObject);
		}else{
		    this.addSymbol(varName, aliasNameObject);
		}
	}
	public void addSymbol(String varName,Object aliasNameObject) throws Exception{
		if(this.symbolTable.containsKey(varName)){
			throw new QLException("变量" + varName + "已经存在，不能重复定义，也不能再从函数内部 exprot ");
		}
		this.symbolTable.put(varName,aliasNameObject);
	}
	public void addSymbol(Map<String,Object> aliasNameObjects) throws Exception{
		this.symbolTable.putAll(aliasNameObjects);
	}
	
	public void setSupportDynamicFieldName(boolean isSupportDynamicFieldName) {
		this.isSupportDynamicFieldName = isSupportDynamicFieldName;
	}
	public boolean isSupportDynamicFieldName(){
		 return this.isSupportDynamicFieldName;
	}
	public ExpressRunner getExpressRunner(){
		return this.runner;
	}
	public Object findAliasOrDefSymbol(String varName)throws Exception{
		Object result =  this.symbolTable.get(varName);
		if(result == null){
			if( this.parent != null && this.parent instanceof InstructionSetContext){
			    result = ((InstructionSetContext)this.parent).findAliasOrDefSymbol(varName);
			}else{
			    result = null;
			}
		}	
		return result;		
	}
	public Object getSymbol(String varName) throws Exception{
		Object result = this.symbolTable.get(varName);
		if( result == null && this.expressLoader != null){
			result = this.expressLoader.getInstructionSet(varName);
		}
		if(result == null){
			if (this.isExpandToParent == true && this.parent != null
					&& this.parent instanceof InstructionSetContext) {
				result = ((InstructionSetContext) this.parent)
						.getSymbol(varName);
			} else {
				result = OperateDataCacheManager.fetchOperateDataAttr(varName, null);
				this.addSymbol(varName, result);
			}
		}	
		return result;
	}
	
	public ExpressLoader getExpressLoader() {
		return expressLoader;
	}

	public IExpressContext<String,Object> getParent(){
		return  this.parent;
	}
	public Object get(Object key){
		if(this.content != null && this.content.containsKey(key)){
			return this.content.get(key);
		}else if(this.isExpandToParent == true && this.parent != null){
			return this.parent.get(key);
		}
		return null;
	}
	public Object put(String key, Object value){
		if(this.content != null && this.content.containsKey(key) ){
			return this.content.put(key,value);
		}else if (this.isExpandToParent == false){
			if(this.content == null){
				this.content = new HashMap<String,Object>();
			}
			return this.content.put(key,value);
		}else if(this.parent != null){
			return this.parent.put(key,value);
		}else{
			throw new RuntimeException("没有定义局部变量：" + key +",而且没有全局上下文");
		}
	}

}
