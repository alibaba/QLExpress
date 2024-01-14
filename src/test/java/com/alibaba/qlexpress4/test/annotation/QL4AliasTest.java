package com.alibaba.qlexpress4.test.annotation;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.MockErrorReporter;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.instruction.GetFieldInstruction;
import com.alibaba.qlexpress4.runtime.instruction.MethodInvokeInstruction;
import com.alibaba.qlexpress4.runtime.instruction.MockQContextParent;
import com.alibaba.qlexpress4.test.property.Child7;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import org.junit.Assert;
import org.junit.Test;

/**
 * Author: TaoKan
 */
public class QL4AliasTest {

    @Test
    public void classFieldTest() throws Exception {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "测试静态字段", false);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        mockQContextParent.push(new DataValue(new MetaClass(Child7.class)));
        getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals((mockQContextParent.getValue()).get(),8);
    }


    @Test
    public void staticMethodTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "测试静态方法",0, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new MetaClass(Child7.class));
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(),11);
    }

    @Test
    public void memberMethodTest() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(
                errorReporter, "测试方法",0, false
        );
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child7());
        mockQContextParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals(mockQContextParent.getValue().get(),10);
    }
}
