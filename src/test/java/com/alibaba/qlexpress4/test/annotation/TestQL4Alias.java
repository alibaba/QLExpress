package com.alibaba.qlexpress4.test.annotation;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.instruction.GetFieldInstruction;
import com.alibaba.qlexpress4.runtime.instruction.MethodInvokeInstruction;
import com.alibaba.qlexpress4.test.TestErrorReporter;
import com.alibaba.qlexpress4.test.instruction.TestQContextParent;
import com.alibaba.qlexpress4.test.property.Child7;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/7/31 上午11:33
 */
public class TestQL4Alias {

    @Test
    public void testQLAliasClassField() throws Exception {
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "测试静态字段");
        TestQContextParent testQContextParent = new TestQContextParent();
        testQContextParent.push(new DataValue(new MetaClass(Child7.class)));
        getFieldInstruction.execute(testQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals((testQContextParent.getValue()).get(),8);
    }


    @Test
    public void testQLAliasClassFunction() throws Exception {
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "测试静态方法",0);
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new MetaClass(Child7.class));
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),11);
    }

    @Test
    public void testQLAliasFunction() throws Exception {
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "测试方法",0);
        TestQContextParent testQContextParent = new TestQContextParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child7());
        testQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQContextParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQContextParent.getValue().get(),10);
    }
}
