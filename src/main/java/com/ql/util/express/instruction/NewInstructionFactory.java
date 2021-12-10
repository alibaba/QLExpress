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
    public boolean createInstruction(ExpressRunner aCompile,
        InstructionSet result, Stack<ForRelBreakContinue> forStack,
        ExpressNode node, boolean isRoot) throws Exception {
        OperatorBase op = aCompile.getOperatorFactory().newInstance("new");
        ExpressNode[] children = node.getChildren();
        if (node.isTypeEqualsOrChild("NEW_ARRAY")) {
            String tempStr = children[0].getValue();
            for (int i = 0; i < children.length - 1; i++) {
                tempStr = tempStr + "[]";
            }
            children[0].setValue(tempStr);
            children[0].setOrgiValue(tempStr);
            children[0].setObjectValue(ExpressUtil.getJavaClass(tempStr));
        } else if (node.isTypeEqualsOrChild("anonymousNewArray")) {
            op = aCompile.getOperatorFactory().newInstance("anonymousNewArray");
        }

        boolean returnVal = false;
        children = node.getChildren();// 需要重新获取数据
        for (int i = 0; i < children.length; i++) {
            boolean tmpHas = aCompile.createInstructionSetPrivate(result, forStack, children[i], false);
            returnVal = returnVal || tmpHas;
        }
        result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine()));
        return returnVal;
    }
}
