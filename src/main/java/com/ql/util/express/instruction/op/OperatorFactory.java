package com.ql.util.express.instruction.op;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.ql.util.express.exception.QLCompileException;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.parse.ExpressNode;

public class OperatorFactory {
    /**
     * 是否需要高精度计算
     */
    private final boolean isPrecise;

    private final Map<String, OperatorBase> operatorMap = new HashMap<>();

    public OperatorFactory(boolean isPrecise) {
        this.isPrecise = isPrecise;
        addOperator("new", new OperatorNew("new"));
        addOperator("anonymousNewArray", new OperatorAnonymousNewArray("anonymousNewArray"));
        addOperator("NewList", new OperatorAnonymousNewList("NewList"));
        addOperator(":", new OperatorKeyValue(":"));
        addOperator("NewMap", new OperatorAnonymousNewMap("NewMap"));
        addOperator("def", new OperatorDef("def"));
        addOperator("exportDef", new OperatorExportDef("exportDef"));
        addOperator("!", new OperatorNot("!"));
        addOperator("*", new OperatorMultiplyDivide("*"));
        addOperator("/", new OperatorMultiplyDivide("/"));
        addOperator("%", new OperatorMultiplyDivide("%"));
        addOperator("mod", new OperatorMultiplyDivide("mod"));
        addOperator("+", new OperatorAdd("+"));
        addOperator("-", new OperatorReduce("-"));
        addOperator("<", new OperatorEqualsLessMore("<"));
        addOperator(">", new OperatorEqualsLessMore(">"));
        addOperator("<=", new OperatorEqualsLessMore("<="));
        addOperator(">=", new OperatorEqualsLessMore(">="));
        addOperator("==", new OperatorEqualsLessMore("=="));
        addOperator("!=", new OperatorEqualsLessMore("!="));
        addOperator("<>", new OperatorEqualsLessMore("<>"));
        addOperator("&&", new OperatorAnd("&&"));
        addOperator("||", new OperatorOr("||"));
        addOperator("nor", new OperatorNor("nor"));
        addOperator("=", new OperatorEvaluate("="));
        addOperator("exportAlias", new OperatorExportAlias("exportAlias"));
        addOperator("alias", new OperatorAlias("alias"));
        addOperator("break", new OperatorBreak("break"));
        addOperator("continue", new OperatorContinue("continue"));
        addOperator("return", new OperatorReturn("return"));
        addOperator("ARRAY_CALL", new OperatorArray("ARRAY_CALL"));
        addOperator("++", new OperatorDoubleAddReduce("++"));
        addOperator("--", new OperatorDoubleAddReduce("--"));
        addOperator("cast", new OperatorCast("cast"));
        addOperator("macro", new OperatorMacro("macro"));
        addOperator("function", new OperatorFunction("function"));
        addOperator("in", new OperatorIn("in"));
        addOperator("like", new OperatorLike("like"));

        // bit operator
        addOperator("&", new OperatorBit("&"));
        addOperator("|", new OperatorBit("|"));
        addOperator("^", new OperatorBit("^"));
        addOperator("~", new OperatorBit("~"));
        addOperator("<<", new OperatorBit("<<"));
        addOperator(">>", new OperatorBit(">>"));
    }

    public void addOperator(String name, OperatorBase operatorBase) {
        OperatorBase existOperator = this.operatorMap.get(name);
        if (existOperator != null) {
            throw new RuntimeException(
                "重复定义操作符：" + name + "定义1：" + existOperator.getClass() + " 定义2：" + operatorBase.getClass());
        }
        operatorBase.setPrecise(this.isPrecise);
        operatorBase.setAliasName(name);
        operatorMap.put(name, operatorBase);
    }

    public OperatorBase replaceOperator(String name, OperatorBase op) {
        OperatorBase old = this.operatorMap.remove(name);
        this.addOperator(name, op);
        return old;
    }

    @SuppressWarnings("unchecked")
    public void addOperatorWithAlias(String aliasName, String name, String errorInfo) throws Exception {
        if (!this.operatorMap.containsKey(name)) {
            throw new QLException(name + " 不是系统级别的操作符号，不能设置别名");
        } else {
            OperatorBase originalOperator = this.operatorMap.get(name);
            if (originalOperator == null) {
                throw new QLException(name + " 不能被设置别名");
            }
            OperatorBase destOperator;
            if (originalOperator instanceof CanClone) {
                destOperator = ((CanClone)originalOperator).cloneMe(aliasName, errorInfo);
            } else {
                Class<OperatorBase> opClass = (Class<OperatorBase>)originalOperator.getClass();
                Constructor<OperatorBase> constructor;
                try {
                    constructor = opClass.getConstructor(String.class, String.class, String.class);
                } catch (Exception e) {
                    throw new QLException(name + " 不能被设置别名:" + e.getMessage());
                }
                if (constructor == null) {
                    throw new QLException(name + " 不能被设置别名");
                }
                destOperator = constructor.newInstance(aliasName, name, errorInfo);
            }
            if (this.operatorMap.containsKey(aliasName)) {
                throw new RuntimeException("操作符号：\"" + aliasName + "\" 已经存在");
            }
            this.addOperator(aliasName, destOperator);
        }
    }

    public boolean isExistOperator(String operatorName) {
        return operatorMap.containsKey(operatorName);
    }

    public OperatorBase getOperator(String operatorName) {
        return this.operatorMap.get(operatorName);
    }

    /**
     * 创建一个新的操作符实例
     */
    public OperatorBase newInstance(ExpressNode opItem) throws Exception {
        OperatorBase op = operatorMap.get(opItem.getNodeType().getName());
        if (op == null) {
            op = operatorMap.get(opItem.getTreeType().getName());
        }
        if (op == null) {
            op = operatorMap.get(opItem.getValue());
        }
        if (op == null) {
            throw new QLCompileException("没有为\"" + opItem.getValue() + "\"定义操作符处理对象");
        }
        return op;
    }

    public OperatorBase newInstance(String opName) throws Exception {
        OperatorBase op = operatorMap.get(opName);
        if (op == null) {
            throw new QLCompileException("没有为\"" + opName + "\"定义操作符处理对象");
        }
        return op;
    }
}
