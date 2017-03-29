package com.ql.util.express.match;

public class NodeTypeManagerTestImpl implements INodeTypeManager {

	public INodeType findNodeType(String name) {
		return new TestNodeTypeImpl(name);
	}

}

class TestNodeTypeImpl implements INodeType{
	String name;
    public TestNodeTypeImpl(String aName){
    	this.name = aName;
    }
	public String getName() {
		return this.name;
	}

	public INodeTypeManager getManager() {
		throw new RuntimeException("没有实现的方法");
	}

	@Override
	public QLPatternNode getPatternNode() {
		throw new RuntimeException("没有实现的方法");
	}
}
