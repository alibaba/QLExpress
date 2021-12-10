package com.ql.util.express.match;

import java.util.ArrayList;
import java.util.List;

public class QLMatchResultTree {
    INodeType matchNodeType;
    IDataNode ref;

    INodeType targetNodeType;
    private List<QLMatchResultTree> left;
    private List<QLMatchResultTree> right;

    public QLMatchResultTree(INodeType aNodeType, IDataNode aRef, INodeType aTargetNodeType) {
        this(aNodeType, aRef);
        this.targetNodeType = aTargetNodeType;
    }

    public QLMatchResultTree(INodeType aNodeType, IDataNode aRef) {
        this.matchNodeType = aNodeType;
        this.ref = aRef;
    }

    public IDataNode getRef() {
        return ref;
    }

    public List<QLMatchResultTree> getLeft() {
        return this.left;
    }

    public void addLeft(QLMatchResultTree node) {
        if (this.left == null) {
            this.left = new ArrayList<>();
        }
        this.left.add(node);
    }

    public void addLeftAll(List<QLMatchResultTree> list) {
        if (this.left == null) {
            this.left = new ArrayList<>();
        }
        this.left.addAll(list);
    }

    public void addRightAll(List<QLMatchResultTree> list) {
        if (this.right == null) {
            this.right = new ArrayList<>();
        }
        this.right.addAll(list);
    }

    public IDataNode transferExpressNodeType(IDataNode sourceNode, INodeType targetType) {
        sourceNode.setNodeType(targetType);
        if (targetType == targetType.getManager().findNodeType("CONST_STRING")) {
            sourceNode.setObjectValue(sourceNode.getValue());
            sourceNode.setTreeType(targetType.getManager().findNodeType("CONST"));
        }
        return sourceNode;
    }

    public void buildExpressNodeTree() {
        if (this.targetNodeType != null) {
            transferExpressNodeType(this.ref, this.targetNodeType);
        }
        if (this.left != null) {
            for (QLMatchResultTree item : left) {
                this.ref.addLeftChild(item.ref);
                item.buildExpressNodeTree();
            }
        }
        if (this.right != null) {
            for (QLMatchResultTree item : right) {
                this.ref.addLeftChild(item.ref);
                item.buildExpressNodeTree();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        printNode(builder, 1);
        return builder.toString();
    }

    public void printNode(StringBuilder builder, int level) {
        builder.append(level + ":");
        for (int i = 0; i < level; i++) {
            builder.append("   ");
        }
        builder.append(ref.getValue() + ":" + this.matchNodeType.getName())
            .append("\n");
        if (this.left != null) {
            for (QLMatchResultTree item : this.left) {
                item.printNode(builder, level + 1);
            }
        }
        if (this.right != null) {
            for (QLMatchResultTree item : this.right) {
                item.printNode(builder, level + 1);
            }
        }
    }
}
