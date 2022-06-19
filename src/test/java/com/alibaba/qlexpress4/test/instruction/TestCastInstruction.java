package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.instruction.CastInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewInstruction;
import com.alibaba.qlexpress4.test.property.Child;
import com.alibaba.qlexpress4.test.property.Parent;
import com.alibaba.qlexpress4.utils.CacheUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/6/15 下午4:48
 */
public class TestCastInstruction {
    @Test
    public void testCastInstruction() {
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
        Object a = (Integer) null;


        System.out.println(a instanceof Boolean);
//        CastInstruction newInstructionForParent0 = new CastInstruction(errorReporter);
//        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
//        newInstructionForParent0.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
//        Object s = testQRuntimeParent.getValue().get();
//        Assert.assertTrue(s instanceof Parent);
    }
}