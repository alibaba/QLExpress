package com.ql.util.express.rule;

/**
 * Created by tianqiao on 16/12/12.
 */
public class Node {
    private Integer nodeId;
    private Integer level;
    private String text;

    public Integer getNodeId() {
        return nodeId;
    }
    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
