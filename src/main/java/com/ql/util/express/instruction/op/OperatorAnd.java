package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.exception.QLException;

/**
 * 处理 And,Or操作
 */

public class OperatorAnd extends Operator {
	public OperatorAnd(String name) {
		this.name = name;
	}
	public OperatorAnd(String aAliasName, String aName, String aErrorInfo) {
		this.name = aName;
		this.aliasName = aAliasName;
		this.errorInfo = aErrorInfo;
	}
	public Object executeInner(Object[] list) throws Exception {
		return executeInner(list[0], list[1]);
	}

	public Object executeInner(Object op1,
			Object op2) throws Exception {

		Object o1 = op1;
		Object o2 = op2;
        boolean r1 = false;
        boolean r2= false;
	    if(o1 == null){
	    	r1 = false;
	    }else if(o1 instanceof Boolean){
        	r1 = ((Boolean) o1).booleanValue();
        }else{
        	String msg = "没有定义类型" + o1 + "和" + o2 + " 的 " + this.name + "操作";
			throw new QLException(msg);
        }
        if(o2 == null){
        	r2 = false;
        }else  if(o2 instanceof Boolean){
        	r2 = ((Boolean) o2).booleanValue();
        }else{
        	String msg = "没有定义类型" + o1 + "和" + o2 + " 的 " + this.name + "操作";
			throw new QLException(msg);
        }
        boolean result = r1 && r2;
		return  Boolean.valueOf(result);

	}
}
