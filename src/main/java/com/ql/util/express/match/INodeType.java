package com.ql.util.express.match;


/**
 * 匹配类型
 * @author xuannan
 *
 */
public interface INodeType {
	public String getName();	
	public INodeTypeManager getManager();
	public QLPatternNode getPatternNode();
}
