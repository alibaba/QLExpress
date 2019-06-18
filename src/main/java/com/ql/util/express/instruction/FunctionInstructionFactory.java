package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;
import com.ql.util.express.parse.ExpressNode;

public class FunctionInstructionFactory extends InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {		
    	ExpressNode[] children = node.getChildren();
    	if(children.length != 3){
    		throw new QLException("funciton 操作符需要3个操作数 " );
    	}
		String functionName =children[0].getValue();
    	ExpressNode[] varDefines = children[1].getChildren();
    	int point =0;

		String  instructionSetType ="";
		if (node.isTypeEqualsOrChild("class")) {
			instructionSetType = InstructionSet.TYPE_CLASS;
		} else {
			instructionSetType = InstructionSet.TYPE_FUNCTION;
		}
		InstructionSet functionSet = new InstructionSet(instructionSetType);
    	
    	while(point<varDefines.length){
    		if(varDefines[point].isTypeEqualsOrChild("def") == false){
    		  throw new QLException("function的参数定义错误," + varDefines[point] + "不是一个Class");
    		}
    		Class<?> varClass = (Class<?>)varDefines[point].getChildren()[0].getObjectValue();
    		String varName = varDefines[point].getChildren()[1].getValue();    		
    		OperateDataLocalVar tmpVar = new OperateDataLocalVar(varName,varClass);
    		functionSet.addParameter(tmpVar);
    		point = point + 1;
    	}
    	
    	ExpressNode functionRoot = new ExpressNode(aCompile.getNodeTypeManager().findNodeType("FUNCTION_DEFINE"),"function-" + functionName);
		for(ExpressNode tempNode :  children[2].getChildren()){
			functionRoot.addLeftChild(tempNode);
		}
		aCompile.createInstructionSet(functionRoot,functionSet);		
		result.addMacroDefine(functionName, new FunctionInstructionSet(functionName,instructionSetType,functionSet));
		return false;
	}
}
