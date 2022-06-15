package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.instruction.NewInstruction;
import com.alibaba.qlexpress4.test.property.Parent;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import com.alibaba.qlexpress4.utils.CacheUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/6/15 下午4:23
 */
public class TestNewInstruction {
    @Test
    public void testNewInstruction() throws Exception{
        ErrorReporter errorReporter = new ErrorReporter() {
            @Override
            public QLRuntimeException report(String errorCode, String reason) {
                return null;
            }

            @Override
            public QLRuntimeException report(String errorCode, String format, Object... args) {
                return null;
            }
        };
        CacheUtil.initCache(128, true);
        NewInstruction newInstructionForParent0 = new NewInstruction(errorReporter, Parent.class, 0);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        newInstructionForParent0.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Object s = testQRuntimeParent.getValue().get();
        Assert.assertTrue(s instanceof Parent);

        NewInstruction newInstructionForParentWithAge = new NewInstruction(errorReporter, Parent.class, 1);
        TestQRuntimeParent testQRuntimeParent1 = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(6);
        testQRuntimeParent1.pushParameter(parentParameters);
        newInstructionForParentWithAge.execute(testQRuntimeParent1, QLOptions.DEFAULT_OPTIONS);
        Object result = testQRuntimeParent1.getValue().get();
        Assert.assertTrue(result instanceof Parent && ((Parent) result).getAge() == 6);
    }
}
