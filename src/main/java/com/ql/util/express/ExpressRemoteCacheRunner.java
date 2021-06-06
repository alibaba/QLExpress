package com.ql.util.express;

import java.util.List;

import org.apache.commons.logging.Log;

/**
 * Remote cache object
 * @author tianqiao
 *
 */
public abstract class ExpressRemoteCacheRunner {



	public void loadCache(String expressName,String text){
		InstructionSet instructionSet;
		try {
			instructionSet = getExpressRunner().parseInstructionSet(text);
			CacheObject cache = new CacheObject();
			cache.setExpressName(expressName);
			cache.setText(text);
			cache.setInstructionSet(instructionSet);
			this.putCache(expressName, cache);
		} catch (Exception e) {
			throw new RuntimeException("An error occurred during the process of parsing instructions and caching.",e);
		}
	}


	public Object execute(String name,IExpressContext<String,Object> context, List<String> errorList,
			boolean isTrace,boolean isCatchException, Log aLog){
		try {
			CacheObject cache = (CacheObject) this.getCache(name);
			if(cache==null){
				throw new RuntimeException("The cache object was not obtained.");
			}
			return getExpressRunner().execute(cache.getInstructionSet(), context, errorList, isTrace, isCatchException, aLog);
		} catch (Exception e) {
			throw new RuntimeException("Obtain cache information, and an error occurred in the execution instruction set.",e);
		}
	}

	/**
	 * Get the executor ExpressRunner
	 * @return
	 */
	public  abstract ExpressRunner getExpressRunner();
	/**
	 * Get the cache object
	 * @param key
	 * @return
	 */
	public abstract Object getCache(String key);
	/**
	 * Place the cached object
	 * @param key
	 * @param object
	 */
	public abstract void putCache(String key,Object object );

}


