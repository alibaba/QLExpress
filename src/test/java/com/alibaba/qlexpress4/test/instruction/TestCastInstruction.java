package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.instruction.CastInstruction;
import com.alibaba.qlexpress4.test.TestErrorReporter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/6/15 下午4:48
 */
public class TestCastInstruction {
    @Test
    public void testCastInstruction() {
        ErrorReporter errorReporter = new TestErrorReporter();
        CastInstruction newInstructionForParent0 = new CastInstruction(errorReporter);
        TestQContextParent testQContextParent = new TestQContextParent();
        TestCastParameters testCastParameters = new TestCastParameters(Integer.class,5L);
        testQContextParent.setParameters(testCastParameters);
        newInstructionForParent0.execute(0, testQContextParent, QLOptions.DEFAULT_OPTIONS);
        Object s = testQContextParent.getValue().get();
        Assert.assertEquals(s,5);
    }
}
