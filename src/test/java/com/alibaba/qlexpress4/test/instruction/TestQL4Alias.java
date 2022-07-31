package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.instruction.GetFieldInstruction;
import com.alibaba.qlexpress4.runtime.instruction.MethodInvokeInstruction;
import com.alibaba.qlexpress4.test.TestErrorReporter;
import com.alibaba.qlexpress4.test.property.Child;
import com.alibaba.qlexpress4.test.property.Child7;
import com.alibaba.qlexpress4.test.property.Parent;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/7/31 上午11:33
 */
public class TestQL4Alias {
    @Test
    public void testQLAliasField() throws Exception {
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "测试字段");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new DataValue(new Child7()));
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        ((LeftValue)testQRuntimeParent.getValue()).set("111",errorReporter);
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),9);
    }

    @Test
    public void testQLAliasClassField() throws Exception {
        ErrorReporter errorReporter = new TestErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "测试静态字段");
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        testQRuntimeParent.push(new DataValue(new MetaClass(Child7.class)));
        getFieldInstruction.execute(testQRuntimeParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals((testQRuntimeParent.getValue()).get(),8);
    }


    @Test
    public void testQLAliasClassFunction() throws Exception {
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "测试静态方法",0);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new MetaClass(Child7.class));
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),11);
    }

    @Test
    public void testQLAliasFunction() throws Exception {
        ErrorReporter errorReporter = new TestErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "测试方法",0);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child7());
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),10);
    }
}
