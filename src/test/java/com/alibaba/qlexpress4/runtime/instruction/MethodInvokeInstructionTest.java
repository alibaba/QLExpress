package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.MockErrorReporter;
import com.alibaba.qlexpress4.test.instruction.TestQContextParent;
import com.alibaba.qlexpress4.test.property.Child;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author: TaoKan
 */
public class MethodInvokeInstructionTest {
    /**
     * child.getMethod1 (methodInstruction+callInstruction)
     * getMethod1
     */
    @Test
    public void equalTypeTest(){
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
        assertEquals(testQContextParent.getValue().get(),3);
    }

    @Test
    public void runnableTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "run",0, false
        );
        TestQContextParent testQContextParent = new TestQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push((Runnable) () -> {});
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
    }

    @Test
    public void defaultMethodTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "returnI",0, false
        );
        TestQContextParent testQContextParent = new TestQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push((InterWithDefaultMethod) () -> 9);
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
        assertEquals(9, testQContextParent.getValue().get());
    }
}
