package com.alibaba.qlexpress4.test.transfer;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.instruction.MethodInvokeInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewInstruction;
import com.alibaba.qlexpress4.test.TestErrorReporter;
import com.alibaba.qlexpress4.test.instruction.TestQRuntimeParent;
import com.alibaba.qlexpress4.test.property.Child;
import com.alibaba.qlexpress4.test.property.Parent;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/7/10 下午4:37
 */
public class TestTransferCases {
    /**
     * type1:int type2:int
     * parent method11(int , int)
     * child method11(long, int)
     * return parent.method1
     */
    @Test
    public void testCastTransferChildMatchMethod() {
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod11",2);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child());
        parentParameters.push(1);
        parentParameters.push(2);
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),3L);
    }
    /**
     * type1:int type2:int
     * parent new Parent(int , int)
     * child new Child(long, int)
     * return (not match)
     */
    @Test
    public void testNewTransferChildMatchConstructor() {
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, Child.class, 2);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(2);
        parentParameters.push(3);
        testQRuntimeParent.pushParameter(parentParameters);
        try {
            newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        }catch (Exception e){
            Assert.assertTrue(e != null);
            return;
        }
        Assert.assertTrue(false);
    }



    /**
     * CastInstruction null obj trans to (boolean)false
     */
    @Test
    public void testCastTransferNullToFalse() {
    }
    /**
     * NewInstruction null obj trans to (boolean)false
     */
    @Test
    public void testNewTransferNullToFalse() {
    }
}
