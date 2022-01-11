package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.parse.ExpressNode;

public class NewInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner expressRunner, InstructionSet result,
        Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot) throws Exception {
        OperatorBase op = expressRunner.getOperatorFactory().newInstance("new");
        ExpressNode[] children = node.getChildrenArray();
        if (node.isTypeEqualsOrChild("NEW_ARRAY")) {
            StringBuilder tempStr = new StringBuilder(children[0].getValue());
            for (int i = 0; i < children.length - 1; i++) {
                tempStr.append("[]");
            }
            children[0].setValue(tempStr.toString());
            children[0].setOriginalValue(tempStr.toString());
            children[0].setObjectValue(ExpressUtil.getJavaClass(tempStr.toString()));
        } else if (node.isTypeEqualsOrChild("anonymousNewArray")) {
            op = expressRunner.getOperatorFactory().newInstance("anonymousNewArray");
        }

        boolean returnVal = false;

        // 需要重新获取数据
        children = node.getChildrenArray();
        for (ExpressNode child : children) {
            boolean tmpHas = expressRunner.createInstructionSetPrivate(result, forStack, child, false);
            returnVal = returnVal || tmpHas;
        }
        result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine()));
        return returnVal;
    }
}
