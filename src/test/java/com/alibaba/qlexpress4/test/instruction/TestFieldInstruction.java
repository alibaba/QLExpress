package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.instruction.GetFieldInstruction;
import com.alibaba.qlexpress4.test.property.Parent;
import com.alibaba.qlexpress4.utils.CacheUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/5/4 上午8:36
 */
public class TestFieldInstruction {
    @Test
    public void testField() throws Exception{
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

        //parent.name notAllowPrivate = null
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "name");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new Parent());
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertNull(testQRuntimeParent.getValue().get());

        //Parent.name notAllowPrivate = null
        testQRuntimeParent.push(new DataValue(Parent.class));
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertNull(testQRuntimeParent.getValue().get());

        //parent.name allowPrivate = example
        testQRuntimeParent.push(new DataValue(new Parent()));
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),"example");

        //Parent.name allowPrivate = null
        testQRuntimeParent.push(new DataValue(Parent.class));
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertNull(testQRuntimeParent.getValue().get());

//
//        GetFieldInstruction getFieldInstruction1 = new GetFieldInstruction(errorReporter, "staticPublic");
//        testQRuntimeParent.push(parent);
//
//        getFieldInstruction1.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
//        Assert.assertTrue(testQRuntimeParent.getValue().get().equals("staticPublic"));
//        testQRuntimeParent.push(parent);
//
//        getFieldInstruction1.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
//        Assert.assertTrue(testQRuntimeParent.getValue().get().equals("staticPublic"));
//
//        GetFieldInstruction getFieldInstruction2 = new GetFieldInstruction(errorReporter, "staticPrivate");
//        testQRuntimeParent.push(parent);
//        getFieldInstruction2.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
//        Assert.assertNull(testQRuntimeParent.getValue().get());
//
//        GetFieldInstruction getFieldInstruction3 = new GetFieldInstruction(errorReporter, "staticGet");
//        testQRuntimeParent.push(parent);
//        getFieldInstruction3.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
//        Assert.assertTrue(testQRuntimeParent.getValue().get().equals("staticGet"));
//
//        GetFieldInstruction getFieldInstruction4 = new GetFieldInstruction(errorReporter, "age");
//        testQRuntimeParent.setParameters(new TestParametersParent());
//        parent.setAge(35);
//        testQRuntimeParent.push(parent);
//        getFieldInstruction4.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
//        Assert.assertTrue((int) testQRuntimeParent.getValue().get() == 35);
//
//        GetFieldInstruction getFieldInstruction5 = new GetFieldInstruction(errorReporter, "sex");
//        testQRuntimeParent.push(parent);
//        getFieldInstruction5.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
//        Assert.assertTrue(testQRuntimeParent.getValue().get().equals("man"));
//
//        GetFieldInstruction getFieldInstruction6 = new GetFieldInstruction(errorReporter, "生日");
//        testQRuntimeParent.push(parent);
//        getFieldInstruction6.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
//        Assert.assertTrue(testQRuntimeParent.getValue().get().equals("2022-01-01"));

    }
}
