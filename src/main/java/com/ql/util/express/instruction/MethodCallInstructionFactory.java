package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.op.OperatorMethod;
import com.ql.util.express.parse.ExpressNode;


public class MethodCallInstructionFactory extends InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,
			InstructionSet result, Stack<ForRelBreakContinue> forStack,
			ExpressNode node, boolean isRoot) throws Exception {
		boolean returnVal = false;
		ExpressNode[] children = node.getChildren();
		//处理对象
		boolean tmpHas = aCompile.createInstructionSetPrivate(result,forStack, children[0], false);
		returnVal = returnVal || tmpHas;
		//处理方法名称
		if(children[1].getNodeType().getName().equalsIgnoreCase("CONST_STRING") == false){
			throw new QLException("对象方法名称不是字符串常量:" + children[1] );
		}
		String methodName = (String)children[1].getObjectValue();
		//处理方法参数
		for (int i = 2; i < children.length; i++) {
			tmpHas = aCompile.createInstructionSetPrivate(result,forStack, children[i], false);
			returnVal = returnVal || tmpHas;
		}
		OperatorBase op = new OperatorMethod(methodName);
		result.addInstruction(new InstructionOperator(op, children.length - 1).setLine(node.getLine()));
		return returnVal;
	}

}
