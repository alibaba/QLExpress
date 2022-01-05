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
    public boolean createInstruction(ExpressRunner expressRunner, InstructionSet result,
        Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot) throws Exception {
        ExpressNode[] children = node.getChildrenArray();
        int arrayDimeCount = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = children.length - 2; i > 0; i--) {
            ExpressNode tmpNode = children[i];
            if (tmpNode.isTypeEqualsOrChild("[]")) {
                arrayDimeCount = arrayDimeCount + 1;
                node.getChildrenList().remove(i);
                stringBuilder.append("[]");
            } else {
                throw new QLCompileException("不正确的类型定义");
            }
        }
        String tempStr = stringBuilder.toString();
        if (arrayDimeCount > 0) {
            node.getChildrenList().get(0).setValue(node.getChildrenList().get(0).getValue() + tempStr);
            node.getChildrenList().get(0).setOriginalValue(node.getChildrenList().get(0).getOriginalValue() + tempStr);
            Object objValue = node.getChildrenList().get(0).getObjectValue();
            if (objValue instanceof Class) {
                Class<?> tmpClass = ExpressUtil.getJavaClass(ExpressUtil.getClassName((Class<?>)objValue) + tempStr);
                node.getChildrenList().get(0).setObjectValue(tmpClass);
            } else {
                node.getChildrenList().get(0).setObjectValue(node.getChildrenList().get(0).getObjectValue() + tempStr);
            }
        }

        children = node.getChildrenArray();
        for (ExpressNode expressNode : children) {
            expressRunner.createInstructionSetPrivate(result, forStack, expressNode, false);
        }
        OperatorBase op = expressRunner.getOperatorFactory().newInstance(node);
        result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine()));
        return true;
    }
}

