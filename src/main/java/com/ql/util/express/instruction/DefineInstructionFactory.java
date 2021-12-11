package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.exception.QLCompileException;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.parse.ExpressNode;

class DefineInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner aCompile, InstructionSet result, Stack<ForRelBreakContinue> forStack,
        ExpressNode node, boolean isRoot) throws Exception {
        ExpressNode[] children = node.getChildren();
        int arrayDimeCount = 0;
        String tempStr = "";
        for (int i = children.length - 2; i > 0; i--) {
            ExpressNode tmpNode = children[i];
            if (tmpNode.isTypeEqualsOrChild("[]")) {
                arrayDimeCount = arrayDimeCount + 1;
                node.getLeftChildren().remove(i);
                tempStr = tempStr + "[]";
            } else {
                throw new QLCompileException("不正确的类型定义");
            }
        }
        if (arrayDimeCount > 0) {
            node.getLeftChildren().get(0).setValue(node.getLeftChildren().get(0).getValue() + tempStr);
            node.getLeftChildren().get(0).setOriginalValue(node.getLeftChildren().get(0).getOriginalValue() + tempStr);
            Object objValue = node.getLeftChildren().get(0).getObjectValue();
            if (objValue instanceof Class) {
                Class<?> tmpClass = ExpressUtil.getJavaClass(ExpressUtil.getClassName((Class<?>)objValue) + tempStr);
                node.getLeftChildren().get(0).setObjectValue(tmpClass);
            } else {
                node.getLeftChildren().get(0).setObjectValue(node.getLeftChildren().get(0).getObjectValue() + tempStr);
            }
        }

        children = node.getChildren();
        for (ExpressNode expressNode : children) {
            aCompile.createInstructionSetPrivate(result, forStack, expressNode, false);
        }
        OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
        result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine()));
        return true;
    }
}

