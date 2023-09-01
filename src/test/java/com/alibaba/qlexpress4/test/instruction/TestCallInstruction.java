package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.MockErrorReporter;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.instruction.CallInstruction;
import com.alibaba.qlexpress4.runtime.instruction.GetMethodInstruction;
import com.alibaba.qlexpress4.test.property.Child;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/6/19 下午3:58
 */
public class TestCallInstruction {
    /**
     * child.getMethod1 (methodInstruction+callInstruction)
     * getMethod1
     */
    @Test
    public void case1(){
        ErrorReporter errorReporter = new MockErrorReporter();
        GetMethodInstruction getMethodInstruction = new GetMethodInstruction(errorReporter, "getMethod1");
        TestQContextParent testQContextParent = new TestQContextParent(true);
        testQContextParent.push(new Child());
        getMethodInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQContextParent.getValue().get() instanceof QLambda);
        QLambda qLambda = (QLambda) testQContextParent.getValue().get();

        CallInstruction callInstruction = new CallInstruction(errorReporter, 2);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(qLambda);
        parentParameters.push(1);
        parentParameters.push(2);
        testQContextParent.setParameters(parentParameters);
        callInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(testQContextParent.getValue().get(),3);

    }
}
