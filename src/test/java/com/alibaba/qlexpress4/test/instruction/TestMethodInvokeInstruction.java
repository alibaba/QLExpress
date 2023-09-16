package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.MockErrorReporter;
import com.alibaba.qlexpress4.runtime.instruction.MethodInvokeInstruction;
import com.alibaba.qlexpress4.test.property.Child;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import org.junit.Assert;
import org.junit.Test;

/**
 * Author: TaoKan
 */
public class TestMethodInvokeInstruction {
    /**
     * child.getMethod1 (methodInstruction+callInstruction)
     * getMethod1
     */
    @Test
    public void case1(){
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "getMethod1",2, false
        );
        TestQContextParent testQContextParent = new TestQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child());
        parentParameters.push(1);
        parentParameters.push(2);
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(testQContextParent.getValue().get(),3);
    }
}
