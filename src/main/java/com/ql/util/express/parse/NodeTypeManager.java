package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ql.util.express.match.INodeTypeManager;

public class NodeTypeManager implements INodeTypeManager {

    public final String[] splitWord;
    private final String[] keyWords;
    private final String[] nodeTypeDefines;
    protected final String[][] instructionFactoryMapping;
    protected final Map<String, NodeType> nodeTypes = new HashMap<>();

    /**
     * 所有的函数定义
     */
    protected final Map<String, String> functions = new HashMap<>();

    public NodeTypeManager() {
        this(new KeyWordDefine4Java());
    }

    public NodeTypeManager(KeyWordDefine4Java keyWordDefine) {
        this.splitWord = keyWordDefine.splitWord;
        WordSplit.sortSplitWord(this.splitWord);
        this.keyWords = keyWordDefine.keyWords;
        this.nodeTypeDefines = keyWordDefine.nodeTypeDefines;
        this.instructionFactoryMapping = keyWordDefine.instructionFactoryMapping;
        this.initial();
        this.addOperatorWithRealNodeType("and", "&&");
        this.addOperatorWithRealNodeType("or", "||");
    }

    public void initial() {
        //创建所有的关键字
        NodeType[] tempKeyWordNodeTypes = new NodeType[splitWord.length + keyWords.length];
        for (int i = 0; i < splitWord.length; i++) {
            tempKeyWordNodeTypes[i] = this.createNodeType(splitWord[i] + ":TYPE=KEYWORD");
        }
        for (int i = 0; i < keyWords.length; i++) {
            tempKeyWordNodeTypes[i + splitWord.length] = this.createNodeType(keyWords[i] + ":TYPE=KEYWORD");
        }
        // 初始化所有的类型信息，
        for (NodeType tempKeyWordNodeType : tempKeyWordNodeTypes) {
            tempKeyWordNodeType.initial();
        }

        // 创建所有的类型信息，但不能初始化
        NodeType[] nodeTypes = new NodeType[nodeTypeDefines.length];
        for (int i = 0; i < nodeTypeDefines.length; i++) {
            nodeTypes[i] = this.createNodeType(nodeTypeDefines[i]);
        }
        // 初始化所有的类型信息，
        for (NodeType nodeType : nodeTypes) {
            nodeType.initial();
        }

        //初始化指令Factory
        if (this.instructionFactoryMapping != null) {
            for (String[] list : this.instructionFactoryMapping) {
                for (String s : list[0].split(",")) {
                    this.findNodeType(s).setInstructionFactory(list[1]);
                }
            }
        }
    }

    /**
     * 创建节点类型，需要注意的是不能初始化，必须所有的类型都创建完成后才能调用初始化方法
     *
     * @param defineStr
     * @return
     */
    public NodeType createNodeType(String defineStr) {
        //避免对操作符号":"的错误处理
        int index = defineStr.indexOf(":", 1);
        String name = defineStr.substring(0, index).trim();
        NodeType define = nodeTypes.get(name);
        if (define != null) {
            throw new RuntimeException("节点类型定义重复:" + name + " 定义1=" + define.getDefineStr() + " 定义2=" + defineStr);
        }
        define = new NodeType(this, name, defineStr);
        nodeTypes.put(name, define);
        return define;
    }

    /**
     * 根据类型名称查找节点类型
     *
     * @param name
     * @return
     */
    @Override
    public NodeType findNodeType(String name) {
        NodeType result = nodeTypes.get(name);
        if (result == null) {
            throw new RuntimeException("没有定义的节点类型：" + name);
        }
        while (result.getRealNodeType() != null) {
            result = result.getRealNodeType();
        }
        return result;
    }

    /**
     * 增加关键字，但是用实际的类型代替，例如 :"如果" -》"if"
     *
     * @param keyWordName
     * @param realName
     */
    public void addOperatorWithRealNodeType(String keyWordName, String realName) {
        NodeType target = this.createNodeType(keyWordName + ":TYPE=KEYWORD,REAL=" + realName);
        target.initial();
    }

    /**
     * 增加新的操作符号，其优先级别，以及语法关系与参照的操作符号一致
     *
     * @param operatorName
     * @param refOperatorName
     * @throws Exception
     */
    public void addOperatorWithLevelOfReference(String operatorName, String refOperatorName) throws Exception {
        NodeType target = this.createNodeType(operatorName + ":TYPE=KEYWORD");
        target.initial();
        NodeType[] list = this.getNodeTypesByKind(NodeTypeKind.OPERATOR);
        NodeType refNodeType = this.findNodeType(refOperatorName);
        target.setInstructionFactory(refNodeType.getInstructionFactory());
        for (NodeType item : list) {
            if (item.isContainerChild(refNodeType)) {
                item.addChild(target);
                return;
            }
        }
    }

    /**
     * 判断是否存在节点类型定义
     *
     * @param name
     * @return
     */
    public NodeType isExistNodeTypeDefine(String name) {
        NodeType result = nodeTypes.get(name);
        if (result != null && result.getRealNodeType() != null) {
            result = result.getRealNodeType();
        }
        return result;
    }

    public NodeType[] getNodeTypesByKind(NodeTypeKind nodeTypeKind) {
        List<NodeType> result = new ArrayList<>();
        for (NodeType item : this.nodeTypes.values()) {
            if (item.getKind() == nodeTypeKind) {
                result.add(item);
            }
        }
        return result.toArray(new NodeType[0]);
    }

    public boolean isFunction(String name) {
        return this.functions.containsKey(name);
    }

    public void addFunctionName(String name) {
        this.functions.put(name, name);
    }
}
