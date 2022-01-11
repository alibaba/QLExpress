package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionGoTo;
import com.ql.util.express.parse.ExpressNode;

public class BreakInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner expressRunner, InstructionSet result,
        Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot) {
        InstructionGoTo breakInstruction = new InstructionGoTo(result.getCurrentPoint() + 1);
        breakInstruction.setName("break");
        forStack.peek().breakList.add(breakInstruction);
        result.addInstruction(breakInstruction.setLine(node.getLine()));
        return false;
    }
}
