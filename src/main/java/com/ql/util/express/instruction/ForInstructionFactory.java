package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.exception.QLCompileException;
import com.ql.util.express.instruction.detail.InstructionCloseNewArea;
import com.ql.util.express.instruction.detail.InstructionGoTo;
import com.ql.util.express.instruction.detail.InstructionGoToWithCondition;
import com.ql.util.express.instruction.detail.InstructionOpenNewArea;
import com.ql.util.express.parse.ExpressNode;

public class ForInstructionFactory extends  InstructionFactory {
	public boolean createInstruction(ExpressRunner aCompile,InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception {		
    	if(node.getChildren().length < 2){
    		throw new QLCompileException("for 操作符至少需要2个操作数 " );
    	}else if(node.getChildren().length > 2){
    		throw new QLCompileException("for 操作符最多只有2个操作数 " );
    	}
    	if(node.getChildren()[0].getChildren()!= null && node.getChildren()[0].getChildren().length > 3){
    		throw new QLCompileException("循环语句的设置不合适:" + node.getChildren()[0]);
    	}
    	//生成作用域开始指令
	    result.addInstruction(new InstructionOpenNewArea().setLine(node.getLine()));			
	    forStack.push(new ForRelBreakContinue());
	    
    	//生成条件语句部分指令
    	ExpressNode conditionNode = node.getChildren()[0];
    	int nodePoint = 0;
    	if (conditionNode.getChildren() != null && conditionNode.getChildren().length == 3){//变量定义，判断，自增都存在
    		int tempPoint = result.getCurrentPoint();
    		aCompile.createInstructionSetPrivate(result,forStack,conditionNode.getChildren()[0],false);
    		if(result.getCurrentPoint() > tempPoint){
    		   nodePoint = nodePoint + 1;
    		}
    	}
    	//循环的开始的位置
    	int loopStartPoint = result.getCurrentPoint()+ 1;
    	
    	//有条件语句
    	InstructionGoToWithCondition conditionInstruction=null;
    	if(conditionNode.getChildren() != null 
    		&& (conditionNode.getChildren().length == 1
    			|| conditionNode.getChildren().length == 2 
    			|| conditionNode.getChildren().length == 3)
    		)	{    		
    		aCompile.createInstructionSetPrivate(result,forStack,conditionNode.getChildren()[nodePoint],false);
    		//跳转的位置需要根据后续的指令情况决定    		
    		conditionInstruction = new InstructionGoToWithCondition(false,-1,true);
    		result.insertInstruction(result.getCurrentPoint()+1,conditionInstruction.setLine(node.getLine()));   
    		nodePoint = nodePoint+ 1;
    	}
    	int conditionPoint = result.getCurrentPoint();
    	//生成循环体的代码
    	aCompile.createInstructionSetPrivate(result,forStack,node.getChildren()[1],false);
    	
    	int selfAddPoint = result.getCurrentPoint()+1;
    	//生成自增代码指令
    	if(conditionNode.getChildren()!= null &&(
    			conditionNode.getChildren().length == 2 || conditionNode.getChildren().length == 3
    			)){
    		aCompile.createInstructionSetPrivate(result,forStack,conditionNode.getChildren()[nodePoint],false);
    	}
    	//增加一个无条件跳转
    	InstructionGoTo reStartGoto = new InstructionGoTo(loopStartPoint - (result.getCurrentPoint() + 1));
    	result.addInstruction(reStartGoto.setLine(node.getLine())); 
    	
    	//修改条件判断的跳转位置
    	if(conditionInstruction != null){
    	   conditionInstruction.setOffset( result.getCurrentPoint() - conditionPoint + 1);
    	}
    	
    	//修改Break和Continue指令的跳转位置,循环出堆
    	ForRelBreakContinue rel =  forStack.pop();
    	for(InstructionGoTo item:rel.breakList){
    		item.setOffset(result.getCurrentPoint() -  item.getOffset()) ;
    	}
    	for(InstructionGoTo item:rel.continueList){
    		item.setOffset(selfAddPoint -  item.getOffset() - 1);
    	}    	
    	
    	//生成作用域结束指令
	    result.addInstruction(new InstructionCloseNewArea().setLine(node.getLine()));

        return false;
	}
}
