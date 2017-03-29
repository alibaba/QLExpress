package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionGoTo;
import com.ql.util.express.parse.ExpressNode;

public class BreakInstructionFactory extends  InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {		
		InstructionGoTo breakInstruction = new InstructionGoTo(result.getCurrentPoint()+1);		
		breakInstruction.name = "break";
		forStack.peek().breakList.add(breakInstruction);
		result.addInstruction(breakInstruction);
		return false;
	}
}
