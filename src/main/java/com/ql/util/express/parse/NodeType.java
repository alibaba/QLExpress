package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ql.util.express.match.INodeType;
import com.ql.util.express.match.QLPattern;
import com.ql.util.express.match.QLPatternNode;

public class NodeType implements INodeType {
    private static final Pattern PATTERN = Pattern.compile("([,:])\\s*(([A-Z]|-|_)*)\\s*=");
    private final NodeTypeManager manager;
    private final String name;
    private final String defineStr;
    private NodeTypeKind kind;
    private NodeType realNodeType;
    private String instructionFactory;

    /**
     * 模式匹配
     */
    private QLPatternNode qlPatternNode;

    protected NodeType(NodeTypeManager nodeTypeManager, String name, String defineStr) {
        this.manager = nodeTypeManager;
        this.defineStr = defineStr;
        this.name = name;
    }

    public static String[][] splitProperties(String str) {
        Matcher matcher = PATTERN.matcher(str);
        List<String[]> list = new ArrayList<>();
        int endIndex = 0;
        while (matcher.find()) {
            if (!list.isEmpty()) {
                list.get(list.size() - 1)[1] = str.substring(endIndex, matcher.start()).trim();
            }
            list.add(new String[2]);
            list.get(list.size() - 1)[0] = str.substring(matcher.start() + 1, matcher.end() - 1).trim();
            endIndex = matcher.end();
        }
        if (!list.isEmpty()) {
            list.get(list.size() - 1)[1] = str.substring(endIndex).trim();
        }
        return list.toArray(new String[0][2]);
    }

    public void initial() {
        try {
            int index = this.defineStr.indexOf(":", 1);
            String[][] properties = splitProperties(this.defineStr.substring(index));
            for (String[] tempList : properties) {
                if ("type".equalsIgnoreCase(tempList[0])) {
                    this.setKind(NodeTypeKind.valueOf(tempList[1]));
                } else if ("real".equalsIgnoreCase(tempList[0])) {
                    this.realNodeType = manager.findNodeType(tempList[1]);
                } else if ("factory".equalsIgnoreCase(tempList[0])) {
                    this.instructionFactory = tempList[1];
                } else if ("define".equalsIgnoreCase(tempList[0])) {
                    this.qlPatternNode = QLPattern.createPattern(this.manager, this.name, tempList[1]);
                } else {
                    throw new RuntimeException(
                        "不能识别\"" + this.name + "\"的属性类型：" + tempList[0] + " 定义：" + this.defineStr);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("节点类型\"" + this.name + "\"初始化失败,定义：" + this.defineStr, e);
        }
    }

    public boolean isEqualsOrChild(String parent) {
        return this.manager.findNodeType(parent).isContainerChild(this);
    }

    public boolean isContainerChild(NodeType child) {
        if (this.equals(child)) {
            return true;
        }
        if (this.qlPatternNode == null) {
            return false;
        }
        if (this.qlPatternNode.isDetailMode()) {
            return ((NodeType)this.qlPatternNode.getNodeType()).isContainerChild(child);
        }
        // 是and类型，不能增加子节点或进行判断
        if (this.qlPatternNode.isAndMode() && !this.qlPatternNode.getChildren().isEmpty()) {
            return false;
        }
        for (QLPatternNode node : this.qlPatternNode.getChildren()) {
            if (node.getNodeType() != null && ((NodeType)node.getNodeType()).isContainerChild(child)) {
                return true;
            }
        }
        return false;
    }

    public void addChild(NodeType child) throws Exception {
        String str = child.name;
        if (this.qlPatternNode != null) {
            str = this.qlPatternNode + "|" + str;
        }
        this.qlPatternNode = QLPattern.createPattern(this.manager, this.name, str);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(name).append(":TYPE=").append(this.kind);
        if (this.instructionFactory != null) {
            result.append(",FACTORY=").append(this.instructionFactory);
        }
        if (this.qlPatternNode != null) {
            result.append(",DEFINE=").append(this.qlPatternNode);
        }
        return result.toString();
    }

    public NodeType getRealNodeType() {
        return realNodeType;
    }

    public NodeTypeKind getKind() {
        return kind;
    }

    public String getInstructionFactory() {
        return instructionFactory;
    }

    public void setInstructionFactory(String instructionFactory) {
        this.instructionFactory = instructionFactory;
    }

    @Override
    public NodeTypeManager getManager() {
        return manager;
    }

    public String getDefineStr() {
        return defineStr;
    }

    public void setKind(NodeTypeKind kind) {
        this.kind = kind;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public QLPatternNode getPatternNode() {
        return this.qlPatternNode;
    }
}

enum NodeTypeKind {
    KEYWORD,
    BLOCK,
    EXPRESS,
    OPERATOR,
    WORDDEF,
    GROUP,
    STATEMENT
}