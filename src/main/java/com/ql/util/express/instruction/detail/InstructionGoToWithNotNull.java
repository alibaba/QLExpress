package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.RunEnvironment;

public class InstructionGoToWithNotNull extends Instruction{
	private static final long serialVersionUID = -2314675800146495935L;
	/**
	 * 跳转指令的偏移量
	 */
    int offset;
    boolean isPopStackData;
    public InstructionGoToWithNotNull(int aOffset,boolean aIsPopStackData){
    	this.offset = aOffset;
    	this.isPopStackData = aIsPopStackData;
    }

	public void execute(RunEnvironment environment,List<String> errorList)throws Exception{
		Object o = null;
		if(this.isPopStackData == false){
		    o = environment.peek().getObject(environment.getContext());	
		}else{
			o = environment.pop().getObject(environment.getContext());	
		}
		if (o != null) {
			if (environment.isTrace() && log.isDebugEnabled()) {
				log.debug("goto +" + this.offset);
			}
			environment.gotoWithOffset(this.offset);
		} else {
			if (environment.isTrace() && log.isDebugEnabled()) {
				log.debug("programPoint ++ ");
			}
			environment.programPointAddOne();
		}
	}

	
	public String toString(){
	  String result = "GoToIf[NOTNULL,isPop=" + this.isPopStackData +"] " ;
	  if(this.offset >=0){
		  result = result +"+";
	  }
	  result = result + this.offset;
	  return result;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public boolean isPopStackData() {
		return isPopStackData;
	}

	public void setPopStackData(boolean isPopStackData) {
		this.isPopStackData = isPopStackData;
	}
	
	
}
