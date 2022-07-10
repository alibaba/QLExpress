package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.instruction.GetFieldInstruction;
import com.alibaba.qlexpress4.test.TestErrorReporter;
import com.alibaba.qlexpress4.test.property.Child;
import com.alibaba.qlexpress4.test.property.Parent;
import com.alibaba.qlexpress4.test.property.TestEnum;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/5/4 上午8:36
 */
public class TestFieldInstruction {
    /**
     * error case(Static method of Parent)
     * Parent::getStaticGet()  setMethod is null and fieldNotAccess
     *
     * @throws Exception
     */
    @Test
    public void case1() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "staticGet");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new DataValue(new MetaClass(Parent.class)));
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),"staticGet1");
     }


    /**
     * public static case(Static field of Parent)
     * Parent.staticSet setAble
     *
     * @throws Exception
     */
    @Test
    public void case2() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "staticSet");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new DataValue(new MetaClass(Parent.class)));
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        ((LeftValue)testQRuntimeParent.getValue()).set("staticSet1");
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),"staticSet1");
    }

    /**
     * private static case(Static field of Parent)
     * Parent.staticSetPrivate notSetAble
     *
     * @throws Exception
     */
    @Test
    public void case3() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "staticSetPrivate");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new DataValue(new MetaClass(Parent.class)));
        try {
            getFieldInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        }catch (Exception e){
            Assert.assertTrue(e != null);
            return;
        }
        Assert.assertTrue(false);
    }

    /**
     * private static case(Static field of Parent)
     * Parent.staticSetPrivate allowSetAble
     *
     * @throws Exception
     */
    @Test
    public void case4() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "staticSetPrivate");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new DataValue(new MetaClass(Parent.class)));
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),"staticSetPrivate");
    }

    /**
     * private static case(Static field of Parent)
     * Parent.staticSetPrivate allowSetAble
     *
     * @throws Exception
     */
    @Test
    public void case5() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "staticSetPrivate");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new DataValue(new MetaClass(Parent.class)));
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),"staticSetPrivate");
    }


    /**
     * error case(Static field of Parent instance)
     * parent.staticFinal fieldSet
     *
     * @throws Exception
     */
    @Test
    public void case6() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "staticFinal");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new Parent());
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        ((LeftValue)testQRuntimeParent.getValue()).set("111");
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),"staticFinal");
    }


    /**
     * static case(Static method of Parent instance)
     * parent.getStaticGet()
     *
     * @throws Exception
     */
    @Test
    public void case7() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "staticGet");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new Parent());
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),"staticGet1");
    }


    /**
     * normal case(normal method of Parent instance)
     * parent.getAge()
     *
     * @throws Exception
     */
    @Test
    public void case8() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "age");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new Parent());
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        ((LeftValue)testQRuntimeParent.getValue()).set(35);
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),35);
    }

    /**
     * normal case(normal field of Parent instance)
     * parent.name accessible
     *
     * @throws Exception
     */
    @Test
    public void case9() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "name");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new Parent());
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        ((LeftValue)testQRuntimeParent.getValue()).set("name1");
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),"name1");
    }


    /**
     * error case(normal field of Parent instance)
     * parent.name with not accessible
     *
     * @throws Exception
     */
    @Test
    public void case10() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "name");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new Parent());
        try {
            getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(false).build());
            ((LeftValue)testQRuntimeParent.getValue()).set("name1");
            Assert.assertEquals((testQRuntimeParent.getValue()).get(),"name1");
        }catch (Exception e){
            Assert.assertTrue(e != null);
            return;
        }
        Assert.assertTrue(false);
    }

    /**
     * normal case(normal method of Parent,Child instance)
     * child.getAge() instead of parent.getAge()
     *
     * @throws Exception
     */
    @Test
    public void case11() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "age");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new Child());
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),11);
    }



    /**
     * normal case(normal method of Parent,Child instance)
     * parent.getBirth() public instead of child.birth(not allow)
     *
     * @throws Exception
     */
    @Test
    public void case12() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "birth");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new Child());
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(false).build());
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),"2022-01-01");
    }



    /**
     * error case(normal method of Parent instance)
     * parent.getMethod1() not findl
     *
     * @throws Exception
     */
    @Test
    public void case13() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "method1");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new Child());
        try {
            getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        }catch (Exception e){
            Assert.assertTrue(e != null);
            return;
        }
        Assert.assertTrue(false);
    }


    /**
     * enum attr get
     * TestEnum.skt.getValue()
     *
     * @throws Exception
     */
    @Test
    public void case14() throws Exception{
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "SKT");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new DataValue(new MetaClass(TestEnum.class)));
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        GetFieldInstruction getFieldInstruction1 = new GetFieldInstruction(errorReporter, "value");
        getFieldInstruction1.execute(testQRuntimeParent,QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),-1);
    }

}
