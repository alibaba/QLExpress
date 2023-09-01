package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.MockErrorReporter;
import com.alibaba.qlexpress4.runtime.instruction.NewInstruction;
import com.alibaba.qlexpress4.test.property.Parent;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/6/15 下午4:23
 */
public class TestNewInstruction {
    @Test
    public void testNewInstruction() throws Exception{
        ErrorReporter errorReporter = new MockErrorReporter();
        NewInstruction newInstructionForParent0 = new NewInstruction(errorReporter, Parent.class, 0);
        TestQContextParent testQContextParent = new TestQContextParent(false);
        newInstructionForParent0.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
        Object s = testQContextParent.getValue().get();
        Assert.assertTrue(s instanceof Parent);

        NewInstruction newInstructionForParentWithAge = new NewInstruction(errorReporter, Parent.class, 1);
        TestQContextParent testQContextParent1 = new TestQContextParent(false);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(6);
        testQContextParent1.pushParameter(parentParameters);
        newInstructionForParentWithAge.execute(testQContextParent1, QLOptions.DEFAULT_OPTIONS);
        Object result = testQContextParent1.getValue().get();
        Assert.assertTrue(result instanceof Parent && ((Parent) result).getAge() == 6);
    }
}
