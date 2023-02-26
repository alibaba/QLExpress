package com.alibaba.qlexpress4.test.transfer;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.instruction.MethodInvokeInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewInstruction;
import com.alibaba.qlexpress4.test.TestErrorReporter;
import com.alibaba.qlexpress4.test.instruction.TestQContextParent;
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child());
        parentParameters.push(1);
        parentParameters.push(2);
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),4L);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child());
        parentParameters.push(1);
        parentParameters.push(2);
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),3L);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(2);
        parentParameters.push(3);
        testQContextParent.pushParameter(parentParameters);
        try {
            newInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(2);
        parentParameters.push(3);
        testQContextParent.pushParameter(parentParameters);
        newInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQContextParent.getValue().get() instanceof Child1);
    }


    /**
     * CastInstruction null obj trans to (boolean)false
     * type1:null
     * child1 method3(boolean)
     * return child1.method3
     */
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
    /**
     * NewInstruction null obj trans to (boolean)false
     * type1:null
     * child1 new(boolean)
     * return child1
     */
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
//    @Test
//    public void testCaseTransferObject(){
//        ErrorReporter errorReporter = new TestErrorReporter();
//        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod4",2);
//        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
//        ParentParameters parentParameters = new ParentParameters();
//        parentParameters.push(new Child1());
//        parentParameters.push(1);
//        parentParameters.push((Integer)null);
//        testQRuntimeParent.setParameters(parentParameters);
//        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
//        Assert.assertEquals(testQRuntimeParent.getValue().get(),2);
//    }

    /**
     * NewInstruction null obj trans to (boolean)false
     * type1:int type2:null
     * child2 new(Object,boolean)
     * return child2
     */
//    @Test
//    public void testNewTransferObject() {
//        ErrorReporter errorReporter = new TestErrorReporter();
//        NewInstruction newInstruction = new NewInstruction(errorReporter, Child2.class, 2);
//        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
//        ParentParameters parentParameters = new ParentParameters();
//        parentParameters.push(1);
//        parentParameters.push(null);
//        testQRuntimeParent.pushParameter(parentParameters);
//        newInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
//        Assert.assertTrue(testQRuntimeParent.getValue().get() instanceof Child2);
//    }

    /**
     * type1:Child3
     * Child3 method5(Parent)
     * return child3.method5
     */
    @Test
    public void testCaseTransferAssigned(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod5",1);
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child3());
        parentParameters.push(new Child3());
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),0);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child3());
        testQContextParent.pushParameter(parentParameters);
        newInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQContextParent.getValue().get() instanceof Child3);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child3());
        parentParameters.push(new Integer[]{5,6});
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),10);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Integer[]{5,6});
        testQContextParent.pushParameter(parentParameters);
        newInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQContextParent.getValue().get() instanceof Child3);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child4());
        parentParameters.push(new Integer(5));
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),5);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Integer(5));
        testQContextParent.pushParameter(parentParameters);
        newInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQContextParent.getValue().get() instanceof Child4);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child5());
        parentParameters.push(5);
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),5.0);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(5);
        testQContextParent.pushParameter(parentParameters);
        newInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQContextParent.getValue().get() instanceof Child5);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child6());
        parentParameters.push(5);
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),5);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(5);
        testQContextParent.pushParameter(parentParameters);
        newInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQContextParent.getValue().get() instanceof Child6);
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child6());
        parentParameters.push(new BigDecimal("5.0"));
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),new BigDecimal("5.0"));
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
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(5.0);
        testQContextParent.pushParameter(parentParameters);
        newInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
    }


    /**
     * type1:int,String,String
     * Child9 addField(int,string...)
     * return child9.addField
     */
    @Test
    public void testCaseTransferVars1(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "addField",3);
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child9());
        parentParameters.push(5);
        parentParameters.push("5.0");
        parentParameters.push("5.0");
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),"1");
    }

    /**
     * type1:int,String,String
     * Child9 new(int,string...)
     * return Child9
     */
    @Test
    public void testNewTransferVars1(){
        ErrorReporter errorReporter = new TestErrorReporter();
        NewInstruction newInstruction = new NewInstruction(errorReporter, Child9.class, 3);
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(5);
        parentParameters.push("5.0");
        parentParameters.push("5.0");
        testQContextParent.pushParameter(parentParameters);
        newInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
    }


    /**
     * type1:int,String,String
     * Child9 addField1(Object...)
     * return child9.addField1
     */
    @Test
    public void testCaseTransferVars2(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "addField1",3);
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child9());
        parentParameters.push(5);
        parentParameters.push("5.0");
        parentParameters.push("5.0");
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),"1");
    }

    /**
     * type1:int,String,String
     * Child9 addField2(object,object...)
     * return child9.addField2
     */
    @Test
    public void testCaseTransferVars3(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "addField2",3);
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child9());
        parentParameters.push(5);
        parentParameters.push("5.0");
        parentParameters.push("5.0");
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),"1");
    }

    /**
     * type1:int,String,String
     * Child9 addField3(object,Integer...)
     * return child9.addField3
     */
    @Test
    public void testCaseTransferVars4(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "addField3",3);
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child9());
        parentParameters.push(5);
        parentParameters.push("asd");
        parentParameters.push("sss");
        testQContextParent.setParameters(parentParameters);
        try {
            methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        }catch (Exception e){
            Assert.assertTrue(e != null);
        }
    }


    /**
     * type1:int,int,int
     * Child9 addField3(object,String...)
     * return child9.addField3
     */
    @Test
    public void testCaseTransferVars5(){
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "addField",3);
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child9());
        parentParameters.push(5);
        parentParameters.push(1);
        parentParameters.push(1);
        testQContextParent.setParameters(parentParameters);
        try {
            methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        }catch (Exception e){
            Assert.assertTrue(e != null);
        }
    }
}
