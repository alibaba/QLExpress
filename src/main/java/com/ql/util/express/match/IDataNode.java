package com.ql.util.express.match;



public interface IDataNode {
	void setNodeType(INodeType type);
	void setTreeType(INodeType findNodeType);
	INodeType getNodeType();
	INodeType getTreeType();

	void addLeftChild(IDataNode ref);
	IDataNode createExpressNode(INodeType aType, String aValue) throws Exception;

	String getValue();
	void setObjectValue(Object value);
}
