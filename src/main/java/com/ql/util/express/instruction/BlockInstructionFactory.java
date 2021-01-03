package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionClearDataStack;
import com.ql.util.express.instruction.detail.InstructionCloseNewArea;
import com.ql.util.express.instruction.detail.InstructionOpenNewArea;
import com.ql.util.express.parse.ExpressNode;

public class BlockInstructionFactory extends InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {	
		if (node.isTypeEqualsOrChild("STAT_SEMICOLON")
				&&result.getCurrentPoint() >=0 &&  result.getInstruction(result.getCurrentPoint()) instanceof InstructionClearDataStack == false) {
			result.addInstruction(new InstructionClearDataStack().setLine(node.getLine()));
		}
		
		int tmpPoint = result.getCurrentPoint()+1;
		boolean returnVal = false;
		boolean hasDef = false;
		for(ExpressNode tmpNode : node.getChildren()){
			boolean tmpHas =   aCompile.createInstructionSetPrivate(result,forStack,tmpNode,false);
			hasDef = hasDef || tmpHas;
		}
		if (hasDef == true&& isRoot == false
				&& node.getTreeType().isEqualsOrChild("STAT_BLOCK")){
			result.insertInstruction(tmpPoint,new InstructionOpenNewArea().setLine(node.getLine()));
			forStack.peek().sign();
			result.insertInstruction(result.getCurrentPoint() + 1,new InstructionCloseNewArea().setLine(node.getLine()));
			returnVal = false;
		}else{
			returnVal = hasDef;
		}
        return returnVal;
	}
}
