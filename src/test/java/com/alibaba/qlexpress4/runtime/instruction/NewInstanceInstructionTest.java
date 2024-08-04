package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.MockErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.test.property.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Author: TaoKan
 */
public class NewInstanceInstructionTest {
    @Test
    public void newInstructionTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        NewInstanceInstruction newInstructionForParent0 = new NewInstanceInstruction(errorReporter, Parent.class, 0);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        newInstructionForParent0.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Object s = mockQContextParent.getValue().get();
        Assert.assertTrue(s instanceof Parent);

        NewInstanceInstruction newInstructionForParentWithAge = new NewInstanceInstruction(errorReporter, Parent.class, 1);
        MockQContextParent mockQContextParent1 = new MockQContextParent(false);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(6);
        mockQContextParent1.pushParameter(parentParameters);
        newInstructionForParentWithAge.execute(mockQContextParent1, QLOptions.DEFAULT_OPTIONS);
        Object result = mockQContextParent1.getValue().get();
        Assert.assertTrue(result instanceof Parent && ((Parent) result).getAge() == 6);
    }

    @Test
    public void constructorNotFoundTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        NewInstanceInstruction newInstruction = new NewInstanceInstruction(errorReporter, Child.class, 2);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(2);
        parentParameters.push(3);
        mockQContextParent.pushParameter(parentParameters);
        try {
            newInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
            Assert.fail();
        } catch (QLRuntimeException e) {
            Assert.assertEquals("CONSTRUCTOR_NOT_FOUND", e.getErrorCode());
        }
    }

    @Test
    public void constructorConvertMatchTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        NewInstanceInstruction newInstruction = new NewInstanceInstruction(errorReporter, Child1.class, 2);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(2);
        parentParameters.push(3);
        mockQContextParent.pushParameter(parentParameters);
        newInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(mockQContextParent.getValue().get() instanceof Child1);
    }

    @Test
    public void constructorConvertAssignedMatch() {
        ErrorReporter errorReporter = new MockErrorReporter();
        NewInstanceInstruction newInstruction = new NewInstanceInstruction(errorReporter, Child3.class, 1);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child3());
        mockQContextParent.pushParameter(parentParameters);
        newInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(mockQContextParent.getValue().get() instanceof Child3);
    }

    @Test
    public void arrayParamTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        NewInstanceInstruction newInstruction = new NewInstanceInstruction(errorReporter, Child3.class, 1);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Integer[]{5,6});
        mockQContextParent.pushParameter(parentParameters);
        newInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(mockQContextParent.getValue().get() instanceof Child3);
    }

    @Test
    public void primitiveParamTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        NewInstanceInstruction newInstruction = new NewInstanceInstruction(errorReporter, Child4.class, 1);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(5);
        mockQContextParent.pushParameter(parentParameters);
        newInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(mockQContextParent.getValue().get() instanceof Child4);
    }

    @Test
    public void primitiveImplicitTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        NewInstanceInstruction newInstruction = new NewInstanceInstruction(errorReporter, Child5.class, 1);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(5);
        mockQContextParent.pushParameter(parentParameters);
        newInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(mockQContextParent.getValue().get() instanceof Child5);
    }

    @Test
    public void bigIntegerImplicitTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        NewInstanceInstruction newInstruction = new NewInstanceInstruction(errorReporter, Child6.class, 1);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(5);
        mockQContextParent.pushParameter(parentParameters);
        newInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(mockQContextParent.getValue().get() instanceof Child6);
    }

    @Test
    public void numberConstructorMatchTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        NewInstanceInstruction newInstruction = new NewInstanceInstruction(errorReporter, NumberConstructor.class, 1);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(5.0);
        mockQContextParent.pushParameter(parentParameters);
        newInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        NumberConstructor numberConstructor = (NumberConstructor) mockQContextParent.getValue().get();
        Assert.assertEquals(0, numberConstructor.getFlag());
    }

    @Test
    public void varArgTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        NewInstanceInstruction newInstruction = new NewInstanceInstruction(errorReporter, Child9.class, 3);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(5);
        parentParameters.push("5.0");
        parentParameters.push("5.0");
        mockQContextParent.pushParameter(parentParameters);
        newInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
    }
}
