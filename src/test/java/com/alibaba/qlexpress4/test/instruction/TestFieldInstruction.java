package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.instruction.GetFieldInstruction;
import com.alibaba.qlexpress4.utils.CacheUtil;
import org.junit.Assert;

/**
 * @Author TaoKan
 * @Date 2022/5/4 上午8:36
 */
public class TestFieldInstruction {
    public static void main(String[] args) {
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
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "name");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.setParameters(new TestParametersParent());
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertNull(testQRuntimeParent.getValue().get());


        testQRuntimeParent.setParameters(new TestParametersParentClass());
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertNull(testQRuntimeParent.getValue().get());
        GetFieldInstruction getFieldInstruction1 = new GetFieldInstruction(errorReporter, "staticPublic");
        getFieldInstruction1.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get().equals("staticPublic"));
        getFieldInstruction1.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get().equals("staticPublic"));

        GetFieldInstruction getFieldInstruction2 = new GetFieldInstruction(errorReporter, "staticPrivate");
        getFieldInstruction2.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertNull(testQRuntimeParent.getValue().get());

        GetFieldInstruction getFieldInstruction3 = new GetFieldInstruction(errorReporter, "staticGet");
        getFieldInstruction3.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get().equals("staticGet"));

        GetFieldInstruction getFieldInstruction4 = new GetFieldInstruction(errorReporter, "age");
        testQRuntimeParent.setParameters(new TestParametersParent());
        getFieldInstruction4.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue((int) testQRuntimeParent.getValue().get() == 35);

        GetFieldInstruction getFieldInstruction5 = new GetFieldInstruction(errorReporter, "sex");
        getFieldInstruction5.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get().equals("man"));

        GetFieldInstruction getFieldInstruction6 = new GetFieldInstruction(errorReporter, "生日");
        getFieldInstruction6.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue(testQRuntimeParent.getValue().get().equals("2022-01-01"));

    }
}
