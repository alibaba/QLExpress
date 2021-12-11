package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.parse.ExpressNode;

public class InInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner aCompile,
        InstructionSet result, Stack<ForRelBreakContinue> forStack,
        ExpressNode node, boolean isRoot) throws Exception {
        ExpressNode[] children = node.getChildren();
        if (children[1].isTypeEqualsOrChild("CHILD_EXPRESS")) {
            node.getLeftChildren().remove(1);
            ExpressNode[] parameterList = children[1].getChildren();
            for (ExpressNode expressNode : parameterList) {
                node.getLeftChildren().add(expressNode);
            }
        }

        boolean returnVal = false;
        children = node.getChildren();
        for (ExpressNode child : children) {
            boolean tmpHas = aCompile.createInstructionSetPrivate(result, forStack, child, false);
            returnVal = returnVal || tmpHas;
        }
        OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
        result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine()));
        return returnVal;
    }
}
