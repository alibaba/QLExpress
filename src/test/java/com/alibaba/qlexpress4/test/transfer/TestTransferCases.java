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
import java.math.BigDecimal;

/**
 * @Author TaoKan
 * @Date 2022/7/10 下午4:37
 */
public class TestTransferCases {
    /**
     * type1:int type2:int
     * parent method11(int , int)
     * child method11(long, int)
     * return child.method11
     */
    @Test
    public void testCastTransferChildMatchMethod() {
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod11",2);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child(),Child.class);
        parentParameters.push(1,int.class);
        parentParameters.push(2,int.class);
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),3L);
    }
    /**
     * type1:int type2:int
     * parent method12(int , int)
     * child method12(Boolean, int)
     * return parent.method12
     */
    @Test
    public void testCastNotTransferChildMatchMethod() {
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod12",2);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child(),Child.class);
        parentParameters.push(1,int.class);
        parentParameters.push(2,int.class);
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),3L);
    }
    /**
     * type1:int type2:int
     * parent new Parent(int , int)
     * child new Child(boolean, int)
     * return (not match)
     */
    @Test
    public void testNewNotTransferChildMatchConstructor() {
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, Child.class, 2);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(2,int.class);
        parentParameters.push(3,int.class);
        testQRuntimeParent.pushParameter(parentParameters);
        try {
            newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        }catch (Exception e){
            Assert.assertTrue(e != null);
            return;
        }
        Assert.assertTrue(false);
    }
    /**
     * type1:int type2:int
     * parent new Parent(int , int)
     * child new Child1(long, int)
     * return new Child1
     */
    @Test
    public void testNewTransferChildMatchConstructor() {
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, Child1.class, 2);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(2,int.class);
        parentParameters.push(3,int.class);
        testQRuntimeParent.pushParameter(parentParameters);
        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child1);
    }


    /**
     * CastInstruction null obj trans to (boolean)false
     * type1:null
     * child1 method3(boolean)
     * return child1.method3
     */
    @Test
    public void testCastTransferNullToFalse() {
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod3",1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child1(),Child1.class);
        parentParameters.push((Integer)null,null);
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),false);
    }
    /**
     * NewInstruction null obj trans to (boolean)false
     * type1:null
     * child1 new(boolean)
     * return child1
     */
    @Test
    public void testNewTransferNullToFalse() {
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, Child1.class, 1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(null,null);
        testQRuntimeParent.pushParameter(parentParameters);
        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child1);
    }

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
        parentParameters.push(new Child1(),Child1.class);
        parentParameters.push(1,int.class);
        parentParameters.push((Integer)null,Integer.class);
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),2);
    }

    /**
     * NewInstruction null obj trans to (boolean)false
     * type1:int type2:null
     * child2 new(Object,boolean)
     * return child2
     */
    @Test
    public void testNewTransferObject() {
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, Child2.class, 2);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(1,int.class);
        parentParameters.push(null,Object.class);
        testQRuntimeParent.pushParameter(parentParameters);
        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child2);
    }

    /**
     * type1:Child3
     * Child3 method5(Parent)
     * return child3.method5
     */
    @Test
    public void testCaseTransferAssigned(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod5",1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child3(),Child3.class);
        parentParameters.push(new Child3(),Child3.class);
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
        parentParameters.push(new Child3(),Child3.class);
        testQRuntimeParent.pushParameter(parentParameters);
        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child3);
    }


    /**
     * type1:Child3
     * Child3 method6(Parent)
     * return child3.method6
     */
    @Test
    public void testCaseTransferArray(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod6",1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child3(),Child3.class);
        parentParameters.push(new Integer[]{5,6},Integer[].class);
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),10);
    }

    /**
     * type1:Integer[]
     * Child3 new(Object[])
     * return Child3
     */
    @Test
    public void testNewTransferArray(){
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, Child3.class, 1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Integer[]{5,6},Integer[].class);
        testQRuntimeParent.pushParameter(parentParameters);
        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child3);
    }

    /**
     * type1:Integer
     * Child4 method7(int)
     * return child4.method7
     */
    @Test
    public void testCaseTransferPrimitive(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod7",1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child4(),Child4.class);
        parentParameters.push(new Integer(5),Integer.class);
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),5);
    }

    /**
     * type1:Integer
     * Child4 new(int)
     * return Child4
     */
    @Test
    public void testNewTransferPrimitive(){
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, Child4.class, 1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Integer(5),Integer.class);
        testQRuntimeParent.pushParameter(parentParameters);
        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child4);
    }

    @Test
    public void testCaseTransferQLambda(){

    }
    @Test
    public void testNewTransferQLambda(){

    }

    /**
     * type1:int
     * Child5 method8(double)
     * return child5.method8
     */
    @Test
    public void testCaseTransferImplicit(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod8",1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child5(),Child5.class);
        parentParameters.push(5,int.class);
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),5.0);
    }

    /**
     * type1:int
     * Child5 new(double)
     * return Child5
     */
    @Test
    public void testNewTransferImplicit(){
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, Child5.class, 1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(5,int.class);
        testQRuntimeParent.pushParameter(parentParameters);
        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child5);
    }

    /**
     * type1:int
     * Child6 method9(BigInteger)
     * return child6.method9
     */
    @Test
    public void testCaseTransferExtend(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod9",1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child6(),Child6.class);
        parentParameters.push(5,int.class);
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),5);
    }

    /**
     * type1:int
     * Child6 new(double)
     * return Child6
     */
    @Test
    public void testNewTransferExtend(){
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, Child6.class, 1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(5,double.class);
        testQRuntimeParent.pushParameter(parentParameters);
        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child6);
    }

    /**
     * type1:BigDecimal
     * Child6 method10(double)
     * return child6.method10
     */
    @Test
    public void testCaseTransferExtend2(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod10",1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child6(), Child6.class);
        parentParameters.push(new BigDecimal("5.0"), BigDecimal.class);
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),new BigDecimal("5.0"));
    }


    /**
     * type1:int
     * Child6 new(double)
     * return Child6
     */
    @Test
    public void testNewTransferBugFix(){
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, BugFixTest1.class, 1);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(5.0,double.class);
        testQRuntimeParent.pushParameter(parentParameters);
        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
    }
}
