package com.ql.util.express.match;


/**
 * Match type
 * @author xuannan
 *
 */
public interface INodeType {
	String getName();
	INodeTypeManager getManager();
	QLPatternNode getPatternNode();
}
