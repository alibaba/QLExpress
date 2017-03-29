package com.ql.util.express.match;



public interface IDataNode {
	public void setNodeType(INodeType type);
	public void setTreeType(INodeType findNodeType);
	public INodeType getNodeType();
	public INodeType getTreeType();

	public void addLeftChild(IDataNode ref);
	public IDataNode createExpressNode(INodeType aType,String aValue) throws Exception;

	public String getValue();
	public void setObjectValue(Object value);
}
