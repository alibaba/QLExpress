package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.MockErrorReporter;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.test.property.Child;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import org.junit.Assert;
import org.junit.Test;

/**
 * Author: TaoKan
 */
public class CallInstructionTest {
    /**
     * child.getMethod1 (methodInstruction+callInstruction)
     * getMethod1
     */
    @Test
    public void case1() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetMethodInstruction getMethodInstruction = new GetMethodInstruction(errorReporter, "getMethod1");
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        mockQContextParent.push(new Child());
        getMethodInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(mockQContextParent.getValue().get() instanceof QLambda);
        QLambda qLambda = (QLambda)mockQContextParent.getValue().get();
        
        CallInstruction callInstruction = new CallInstruction(errorReporter, 2);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(qLambda);
        parentParameters.push(1);
        parentParameters.push(2);
        mockQContextParent.setParameters(parentParameters);
        callInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(), 3);
    }
}
