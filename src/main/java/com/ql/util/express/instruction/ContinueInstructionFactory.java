package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionGoTo;
import com.ql.util.express.parse.ExpressNode;

public class ContinueInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner expressRunner, InstructionSet result,
        Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot) {
        InstructionGoTo continueInstruction = new InstructionGoTo(result.getCurrentPoint() + 1);
        continueInstruction.setName("continue");
        forStack.peek().continueList.add(continueInstruction);
        result.addInstruction(continueInstruction.setLine(node.getLine()));
        return false;
    }
}
