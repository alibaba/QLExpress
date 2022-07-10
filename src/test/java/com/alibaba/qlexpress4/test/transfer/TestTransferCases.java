package com.alibaba.qlexpress4.test.transfer;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.instruction.MethodInvokeInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewInstruction;
import com.alibaba.qlexpress4.test.TestErrorReporter;
import com.alibaba.qlexpress4.test.instruction.TestQRuntimeParent;
import com.alibaba.qlexpress4.test.property.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/7/10 下午4:37
 */
public class TestTransferCases {
//    /**
//     * type1:int type2:int
//     * parent method11(int , int)
//     * child method11(long, int)
//     * return child.method11
//     */
//    @Test
//    public void testCastTransferChildMatchMethod() {
//        ErrorReporter errorReporter = new TestErrorReporter();
//        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod11",2);
//        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
//        ParentParameters parentParameters = new ParentParameters();
//        parentParameters.push(new Child());
//        parentParameters.push(1);
//        parentParameters.push(2);
//        testQRuntimeParent.setParameters(parentParameters);
//        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
//        Assert.assertEquals(testQRuntimeParent.getValue().get(),3L);
//    }
//    /**
//     * type1:int type2:int
//     * parent method12(int , int)
//     * child method12(Boolean, int)
//     * return parent.method12
//     */
//    @Test
//    public void testCastNotTransferChildMatchMethod() {
//        ErrorReporter errorReporter = new TestErrorReporter();
//        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod12",2);
//        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
//        ParentParameters parentParameters = new ParentParameters();
//        parentParameters.push(new Child());
//        parentParameters.push(1);
//        parentParameters.push(2);
//        testQRuntimeParent.setParameters(parentParameters);
//        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
//        Assert.assertEquals(testQRuntimeParent.getValue().get(),3L);
//    }
//    /**
//     * type1:int type2:int
//     * parent new Parent(int , int)
//     * child new Child(boolean, int)
//     * return (not match)
//     */
//    @Test
//    public void testNewNotTransferChildMatchConstructor() {
//        ErrorReporter errorReporter = new TestErrorReporter();
//        NewInstruction newInstruction = new NewInstruction(errorReporter, Child.class, 2);
//        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
//        ParentParameters parentParameters = new ParentParameters();
//        parentParameters.push(2);
//        parentParameters.push(3);
//        testQRuntimeParent.pushParameter(parentParameters);
//        try {
//            newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
//        }catch (Exception e){
//            Assert.assertTrue(e != null);
//            return;
//        }
//        Assert.assertTrue(false);
//    }
//    /**
//     * type1:int type2:int
//     * parent new Parent(int , int)
//     * child new Child1(long, int)
//     * return new Child1
//     */
//    @Test
//    public void testNewTransferChildMatchConstructor() {
//        ErrorReporter errorReporter = new TestErrorReporter();
//        NewInstruction newInstruction = new NewInstruction(errorReporter, Child1.class, 2);
//        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
//        ParentParameters parentParameters = new ParentParameters();
//        parentParameters.push(2);
//        parentParameters.push(3);
//        testQRuntimeParent.pushParameter(parentParameters);
//        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
//        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child1);
//    }
//
//
//    /**
//     * CastInstruction null obj trans to (boolean)false
//     * type1:null
//     * child1 method3(boolean)
//     * return child1.method3
//     */
//    @Test
//    public void testCastTransferNullToFalse() {
//        ErrorReporter errorReporter = new TestErrorReporter();
//        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod3",1);
//        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
//        ParentParameters parentParameters = new ParentParameters();
//        parentParameters.push(new Child1());
//        parentParameters.push((Integer)null);
//        testQRuntimeParent.setParameters(parentParameters);
//        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
//        Assert.assertEquals(testQRuntimeParent.getValue().get(),false);
//    }
//    /**
//     * NewInstruction null obj trans to (boolean)false
//     * type1:null
//     * child1 new(boolean)
//     * return child1
//     */
//    @Test
//    public void testNewTransferNullToFalse() {
//        ErrorReporter errorReporter = new TestErrorReporter();
//        NewInstruction newInstruction = new NewInstruction(errorReporter, Child1.class, 1);
//        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
//        ParentParameters parentParameters = new ParentParameters();
//        parentParameters.push(null);
//        testQRuntimeParent.pushParameter(parentParameters);
//        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
//        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child1);
//    }

    /**
     * type1:int type2:null
     * child1 method4(Object,boolean)
     * return child1.method4
     */
    @Test
    public void testCaseTransferObject(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod4",2);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child1());
        parentParameters.push(1);
        parentParameters.push((Integer)null);
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),2);
    }

    /**
     * NewInstruction null obj trans to (boolean)false
     * type1:int type2:null
     * child1 new(Object,boolean)
     * return child1
     */
    @Test
    public void testNewTransferObject() {
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, Child2.class, 2);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(1);
        parentParameters.push(null);
        testQRuntimeParent.pushParameter(parentParameters);
        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child2);
    }

    /**
     * type1:Child1
     * Child1 method5(Parent)
     * return child1.method5
     */
    @Test
    public void testCaseTransferAssigned(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod5",1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child3());
        parentParameters.push(new Child3());
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),0);
    }

    /**
     * type1:Child3
     * Child3 new(Parent)
     * return Child3
     */
    @Test
    public void testNewTransferAssigned(){
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, Child3.class, 1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child3());
        testQRuntimeParent.pushParameter(parentParameters);
        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child3);
    }

    @Test
    public void testCaseTransferArray(){

    }
    @Test
    public void testNewTransferArray(){

    }
    @Test
    public void testCaseTransferPrimitive(){

    }
    @Test
    public void testNewTransferPrimitive(){

    }
    @Test
    public void testCaseTransferQLambda(){

    }
    @Test
    public void testNewTransferQLambda(){

    }
    @Test
    public void testCaseTransferImplicit(){

    }
    @Test
    public void testNewTransferImplicit(){

    }
    @Test
    public void testCaseTransferExtend(){

    }
    @Test
    public void testNewTransferExtend(){

    }
}
