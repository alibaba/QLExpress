package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.MockErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.test.property.*;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

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
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child());
        parentParameters.push(1);
        parentParameters.push(2);
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        assertEquals(mockQContextParent.getValue().get(),3);
    }

    @Test
    public void runnableTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "run",0, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push((Runnable) () -> {});
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
    }

    @Test
    public void defaultMethodTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "returnI",0, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push((InterWithDefaultMethod) () -> 9);
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        assertEquals(9, mockQContextParent.getValue().get());
    }

    @Test
    public void childMethodMatchTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "getMethod11",2, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child());
        parentParameters.push(1);
        parentParameters.push(2);
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(),3L);
    }

    @Test
    public void parentMethodMatch() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "getMethod12",2, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child());
        parentParameters.push(1);
        parentParameters.push(2);
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(),3L);
    }

    @Test
    public void convertTypeAssignedMatch() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "getMethod5",1, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child3());
        parentParameters.push(new Child3());
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(),0);
    }

    @Test
    public void arrayParamTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "getMethod6",1, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child3());
        parentParameters.push(new Integer[]{5,6});
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(),10);
    }

    @Test
    public void primitiveParamTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "getMethod7",1, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child4());
        parentParameters.push(new Integer(5));
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(),5);
    }

    @Test
    public void primitiveImplicitTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "getMethod8",1, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child5());
        parentParameters.push(5);
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(),5.0);
    }

    @Test
    public void bigIntegerImplicitTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "getMethod9",1, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child6());
        parentParameters.push(5);
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(),5);
    }

    @Test
    public void doubleMatchTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "getMethod10",1, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child6());
        parentParameters.push(5.0f);
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(), new BigDecimal("5.0"));
    }

    @Test
    public void varArgTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "addField",3, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child9());
        parentParameters.push(5);
        parentParameters.push("5.0");
        parentParameters.push("5.0");
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(),"1");
    }

    @Test
    public void varArgTest2() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "addField1",3, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child9());
        parentParameters.push(5);
        parentParameters.push("5.0");
        parentParameters.push("5.0");
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(),"1");
    }

    @Test
    public void varArgTest3() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "addField2",3, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child9());
        parentParameters.push(5);
        parentParameters.push("5.0");
        parentParameters.push("5.0");
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(),"1");
    }

    @Test
    public void varArgNotMatchTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "addField3",3, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child9());
        parentParameters.push(5);
        parentParameters.push("asd");
        parentParameters.push("sss");
        mockQContextParent.setParameters(parentParameters);
        try {
            methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
            Assert.fail();
        } catch (QLRuntimeException e){
            Assert.assertEquals("METHOD_NOT_FOUND", e.getErrorCode());
        }
    }

    @Test
    public void varArgNotMatchTest2() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "addField",3, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child9());
        parentParameters.push(5);
        parentParameters.push(1);
        parentParameters.push(1);
        mockQContextParent.setParameters(parentParameters);
        try {
            methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        } catch (QLRuntimeException e){
            Assert.assertEquals("METHOD_NOT_FOUND", e.getErrorCode());
        }
    }
}
