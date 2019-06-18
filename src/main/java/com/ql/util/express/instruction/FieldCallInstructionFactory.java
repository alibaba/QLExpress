package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.exception.QLCompileException;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.op.OperatorField;
import com.ql.util.express.parse.ExpressNode;


public class FieldCallInstructionFactory  extends InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,
			InstructionSet result, Stack<ForRelBreakContinue> forStack,
			ExpressNode node, boolean isRoot) throws Exception {
		boolean returnVal = false;
		ExpressNode[] children = node.getChildren();
		//处理对象
		boolean tmpHas = aCompile.createInstructionSetPrivate(result,forStack, children[0], false);
		returnVal = returnVal || tmpHas;
		
		//处理属性名称
		if(children[1].getNodeType().getName().equalsIgnoreCase("CONST_STRING") == false){
			throw new QLCompileException("对象属性名称不是字符串常量:" + children[1] );
		}
		
		String fieldName = (String)children[1].getObjectValue();
		
		
		OperatorBase op = new OperatorField(fieldName);
		result.addInstruction(new InstructionOperator(op,1).setLine(node.getLine()));
		return returnVal;
	}

}
