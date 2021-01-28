package com.ql.util.express;

/**
 * Load expression resource interface
 * @author xuannan
 *
 */
public interface IExpressResourceLoader {
	/**
	 * Get the content of the expression based on the expression name
	 * @param expressName
	 * @return
	 * @throws Exception
	 */
	public String loadExpress(String expressName) throws Exception;
}
