package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.parse.ExpressNode;

class KeyValueInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner expressRunner, InstructionSet result,
        Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
        throws Exception {
        boolean returnVal = false;
        ExpressNode[] children = node.getChildrenArray();
        if (node.getParent() != null && node.getParent().isTypeEqualsOrChild("STATEMENT")) {
            children[0].setNodeType(expressRunner.getNodeTypeManager().findNodeType("CONST_STRING"));
            children[0].setTreeType(expressRunner.getNodeTypeManager().findNodeType("CONST"));
            children[0].setObjectValue(children[0].getValue());
        }
        if (node.getParent() != null && node.getParent().isTypeEqualsOrChild("STATEMENT")
            && children[1].isTypeEqualsOrChild("STAT_BLOCK")) {
            returnVal = new MacroInstructionFactory().createInstruction(expressRunner, result, forStack, node, isRoot);
        } else if (node.getParent() != null && node.getParent().isTypeEqualsOrChild("STATEMENT")) {
            for (ExpressNode tmpNode : children) {
                boolean tmpHas = expressRunner.createInstructionSetPrivate(result, forStack, tmpNode, false);
                returnVal = returnVal || tmpHas;
            }
            OperatorBase op = expressRunner.getOperatorFactory().newInstance("alias");
            result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine()));
            returnVal = true;
        } else {
            for (ExpressNode tmpNode : children) {
                boolean tmpHas = expressRunner.createInstructionSetPrivate(result, forStack, tmpNode, false);
                returnVal = returnVal || tmpHas;
            }

            OperatorBase op = expressRunner.getOperatorFactory().newInstance(node);
            result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine()));
        }
        return returnVal;
    }
}

