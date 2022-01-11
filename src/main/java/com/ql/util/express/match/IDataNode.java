package com.ql.util.express.match;

public interface IDataNode {
    void setNodeType(INodeType iNodeType);

    void setTreeType(INodeType iNodeType);

    INodeType getNodeType();

    INodeType getTreeType();

    void addChild(IDataNode ref);

    IDataNode createExpressNode(INodeType iNodeType, String value) throws Exception;

    String getValue();

    void setObjectValue(Object value);
}
