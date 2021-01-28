package com.ql.util.express;

import java.util.HashMap;
import java.util.Map;

/**
 * As an expression
 * @author tianqiao
 *
 */
public class LocalExpressCacheRunner extends ExpressRemoteCacheRunner{
	
	private static Map<String,Object> expressMap = new HashMap<String,Object> ();
	
	private ExpressRunner expressRunner;
	
	public LocalExpressCacheRunner(ExpressRunner expressRunner){
		this.expressRunner = expressRunner;		
	}
	@Override
	public final Object getCache(String key) {
		return expressMap.get(key);
	}

	@Override
	public final void putCache(String key, Object object) {
		expressMap.put(key, object);		
	}

	@Override
	public final ExpressRunner getExpressRunner() {
		return this.expressRunner;
	}

}
