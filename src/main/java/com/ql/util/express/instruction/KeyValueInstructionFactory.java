package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.parse.ExpressNode;


class KeyValueInstructionFactory  extends InstructionFactory{
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {
		boolean returnVal = false;		
		ExpressNode[] children = node.getChildren();
		if( node.getParent() != null && node.getParent().isTypeEqualsOrChild("STATEMENT")){
			children[0].setNodeType(aCompile.getNodeTypeManager().findNodeType("CONST_STRING"));
			children[0].setTreeType(aCompile.getNodeTypeManager().findNodeType("CONST"));
			children[0].setObjectValue(children[0].getValue());			
		}
		if( node.getParent() != null && node.getParent().isTypeEqualsOrChild("STATEMENT") && children[1].isTypeEqualsOrChild("STAT_BLOCK")){
			returnVal = new MacroInstructionFactory().createInstruction(aCompile, result, forStack, node, isRoot);
		} else if (node.getParent() != null&& node.getParent().isTypeEqualsOrChild("STATEMENT")) {
			for(int i =0;i < children.length;i++){
				ExpressNode tmpNode = children[i];
				boolean tmpHas =    aCompile.createInstructionSetPrivate(result,forStack,tmpNode,false);
				returnVal = returnVal || tmpHas;
			}
			OperatorBase op = aCompile.getOperatorFactory().newInstance("alias");
			result.addInstruction(new InstructionOperator(op, children.length));
			returnVal = true;
		}else{	
			for(int i =0;i < children.length;i++){
				ExpressNode tmpNode = children[i];
				boolean tmpHas =    aCompile.createInstructionSetPrivate(result,forStack,tmpNode,false);
				returnVal = returnVal || tmpHas;
			}

			OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
			result.addInstruction(new InstructionOperator(op,children.length));
		}
		return returnVal;
	}
}

