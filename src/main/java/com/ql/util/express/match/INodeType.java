package com.ql.util.express.match;

/**
 * 匹配类型
 *
 * @author xuannan
 */
public interface INodeType {
    String getName();

    INodeTypeManager getManager();

    QLPatternNode getPatternNode();
}
