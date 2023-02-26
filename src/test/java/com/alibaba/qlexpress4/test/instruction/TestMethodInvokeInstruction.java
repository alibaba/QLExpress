package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.instruction.MethodInvokeInstruction;
import com.alibaba.qlexpress4.test.TestErrorReporter;
import com.alibaba.qlexpress4.test.property.Child;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/5/4 上午8:36
 */
public class TestMethodInvokeInstruction {
    /**
     * child.getMethod1 (methodInstruction+callInstruction)
     * getMethod1
     */
    @Test
    public void case1(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod1",2);
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child());
        parentParameters.push(1);
        parentParameters.push(2);
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),3);
    }
}
