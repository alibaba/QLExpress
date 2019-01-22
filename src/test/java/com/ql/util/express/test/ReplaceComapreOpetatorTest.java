package com.ql.util.express.test;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.op.OperatorLike;
import org.junit.Test;

/**
 * Created by tianqiao on 18/4/3.
 */
public class ReplaceComapreOpetatorTest {
    
    @Test
    public void testReplaceOperatorTest() throws Exception {
        String express = "null > 1 || null < 1 || null == 1 || null >= 1 || null <= 1 ||null like '%222%'";
        ExpressRunner runner = new ExpressRunner();
        runner.replaceOperator("<",new NullableOperatorEqualsLessMore("<"));
        runner.replaceOperator(">",new NullableOperatorEqualsLessMore(">"));
        runner.replaceOperator("<=",new NullableOperatorEqualsLessMore("<="));
        runner.replaceOperator(">=",new NullableOperatorEqualsLessMore(">="));
        runner.replaceOperator("==",new NullableOperatorEqualsLessMore("=="));
        runner.replaceOperator("!=",new NullableOperatorEqualsLessMore("!="));
        runner.replaceOperator("<>",new NullableOperatorEqualsLessMore("<>"));
        runner.replaceOperator("like",new NullableOperatorLike("like"));
        Object r = runner.execute(express, null, null,false, false);
        System.out.println(r);
    }
    
    public class NullableOperatorLike extends OperatorLike {
        public NullableOperatorLike(String name) {
            super(name);
            
        }
        
        public Object executeInner(Object op1,Object op2) throws Exception {
            if(op1==null||op2==null){
                return false;
            }
            return super.executeInner(op1,op2);
        }
    }
}
