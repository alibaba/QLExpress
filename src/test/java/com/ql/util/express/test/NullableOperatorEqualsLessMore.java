package com.ql.util.express.test;

import com.ql.util.express.instruction.op.OperatorEqualsLessMore;

/**
 * Created by tianqiao on 18/4/3.
 */

    
public class NullableOperatorEqualsLessMore extends OperatorEqualsLessMore {
    
    public NullableOperatorEqualsLessMore(String aName) {
        super(aName);
    }
    
    public NullableOperatorEqualsLessMore(String aAliasName, String aName, String aErrorInfo) {
        super(aAliasName, aName, aErrorInfo);
    }
    
    public Object executeInner(Object op1,Object op2) throws Exception {
        boolean result = executeInner(this.name, op1, op2);
        return result;
    }
    
    public static boolean executeInner(String opStr, Object obj1, Object obj2)
            throws Exception {
        if(obj1==null||obj2==null){
            return false;
        }
        return OperatorEqualsLessMore.executeInner(opStr,obj1,obj2);
    }
    
    
}
    