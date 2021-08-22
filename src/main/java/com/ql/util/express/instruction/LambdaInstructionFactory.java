package com.ql.util.express.instruction;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.detail.InstructionLoadLambda;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;

public class LambdaInstructionFactory extends InstructionFactory {

    private static final String LAMBDA_NODE_NAME = "LAMBDA";

    @Override
    public boolean createInstruction(ExpressRunner aCompile, InstructionSet result, Stack<ForRelBreakContinue> forStack,
                                     ExpressNode node, boolean isRoot) throws Exception {
        ExpressNode[] children = node.getChildren();
        if (children.length != 2) {
            throw new QLException("lambda 操作符需要2个操作数");

        }

        InstructionSet lambdaSet = new InstructionSet(InstructionSet.TYPE_FUNCTION);

        // lambda 参数列表
        ExpressNode lambdaVarDefine = children[0];
        if ("CHILD_EXPRESS".equals(lambdaVarDefine.getNodeType().getName())) {
            // 带括号的参数写法
            for (ExpressNode varDefine : lambdaVarDefine.getChildren()) {
                OperateDataLocalVar tmpVar = new OperateDataLocalVar(varDefine.getValue(), null);
                lambdaSet.addParameter(tmpVar);
            }
        } else {
            // 单参数省略括号的写法
            lambdaSet.addParameter(new OperateDataLocalVar(lambdaVarDefine.getValue(), null));
        }

        // lambda 逻辑体
        ExpressNode lambdaBodyRoot = new ExpressNode(aCompile.getNodeTypeManager()
                .findNodeType("FUNCTION_DEFINE"), LAMBDA_NODE_NAME);
        if ("STAT_BLOCK".equals(node.getNodeType().getName())) {
            for (ExpressNode tempNode : children[1].getChildren()) {
                lambdaBodyRoot.addLeftChild(tempNode);
            }
        } else {
            lambdaBodyRoot.addLeftChild(children[1]);
        }

        aCompile.createInstructionSet(lambdaBodyRoot, lambdaSet);
        result.addInstruction(new InstructionLoadLambda(lambdaSet));
        return false;
    }
}
