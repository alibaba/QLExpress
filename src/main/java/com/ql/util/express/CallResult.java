package com.ql.util.express;

public class CallResult{
	private Object returnValue;
	private boolean isExit;
	public CallResult(Object aReturnValue,boolean aIsExit){
		this.initial(aReturnValue, aIsExit);
	}
	public void initial(Object aReturnValue,boolean aIsExit){
		this.returnValue = aReturnValue;
		this.isExit = aIsExit;
	}
	public void clear(){
		this.returnValue = null;
		this.isExit = false;
	}
	public Object getReturnValue() {
		return returnValue;
	}
	public boolean isExit() {
		return isExit;
	}
	
}

