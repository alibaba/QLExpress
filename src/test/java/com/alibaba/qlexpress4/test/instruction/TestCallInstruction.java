package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.instruction.CallInstruction;
import com.alibaba.qlexpress4.runtime.instruction.GetMethodInstruction;
import com.alibaba.qlexpress4.test.TestErrorReporter;
import com.alibaba.qlexpress4.test.property.Child;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import com.alibaba.qlexpress4.utils.CacheUtil;
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
        ErrorReporter errorReporter = new TestErrorReporter();
        GetMethodInstruction getMethodInstruction = new GetMethodInstruction(errorReporter, "getMethod1");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new Child());
        getMethodInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof QLambda);
        QLambda qLambda = (QLambda)testQRuntimeParent.getValue().get();

        CallInstruction callInstruction = new CallInstruction(errorReporter, 2);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(qLambda);
        parentParameters.push(1);
        parentParameters.push(2);
        testQRuntimeParent.setParameters(parentParameters);
        callInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),3);

    }
}
