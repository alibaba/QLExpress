package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.exception.QLCompileException;
import com.ql.util.express.instruction.detail.InstructionGoTo;
import com.ql.util.express.instruction.detail.InstructionGoToWithCondition;
import com.ql.util.express.parse.ExpressNode;

public class IfInstructionFactory extends  InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {		
		ExpressNode[] oldChildren = node.getChildren();
    	if(oldChildren.length < 2){
    		throw new QLCompileException("if 操作符至少需要2个操作数 " );
    	}else if(oldChildren.length > 5){
        		throw new QLCompileException("if 操作符最多只有5个操作数 " );
    	}
    	ExpressNode[] children = new ExpressNode[3];
    	int point = 0;    	
    	for(int i=0;i<oldChildren.length;i++){
    		if(oldChildren[i].isTypeEqualsOrChild("then")
    		||oldChildren[i].isTypeEqualsOrChild("else")
    		||oldChildren[i].isTypeEqualsOrChild("?")
    		||oldChildren[i].isTypeEqualsOrChild(":")){
    			continue;
    		}
    		children[point] = oldChildren[i];
    		point = point + 1;
    	}
    	if(point == 2){
    		children[2] = new ExpressNode(aCompile.getNodeTypeManager().findNodeType("STAT_BLOCK"),null);  
    	}    	
		int [] finishPoint = new int[children.length];

   		boolean r1 = aCompile.createInstructionSetPrivate(result,forStack,children[0],false);//condition
		InstructionGoToWithCondition goToFalseBody = new InstructionGoToWithCondition(false,
				0,true);
		goToFalseBody.setLine(node.getLine());
		result.addInstruction(goToFalseBody);
		finishPoint[0] = result.getCurrentPoint();

		boolean r2 = aCompile.createInstructionSetPrivate(result,forStack,children[1],false);//true
		InstructionGoTo goToEnd = new InstructionGoTo(0);
		goToEnd.setLine(node.getLine());
		result.addInstruction(goToEnd);
		goToFalseBody.setOffset(result.getCurrentPoint() - finishPoint[0] + 1);

		finishPoint[1] = result.getCurrentPoint();
		boolean r3 = aCompile.createInstructionSetPrivate(result,forStack,children[2],false);//false
		goToEnd.setOffset(result.getCurrentPoint() - finishPoint[1] + 1);
        return r1 || r2 || r3;
	}
}
