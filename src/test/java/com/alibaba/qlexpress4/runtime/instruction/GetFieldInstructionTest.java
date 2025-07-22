package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.MockErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.test.property.Child;
import com.alibaba.qlexpress4.test.property.Parent;
import com.alibaba.qlexpress4.test.property.TestEnum;
import org.junit.Assert;
import org.junit.Test;

/**
 * Author: TaoKan
 */
public class GetFieldInstructionTest {
    /**
     * Parent::getStaticGet()  setMethod is null and fieldNotAccess
     */
    @Test
    public void case1() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "staticGet", false);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        mockQContextParent.push(new DataValue(new MetaClass(Parent.class)));
        getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals((mockQContextParent.getValue()).get(), "staticGet1");
    }
    
    /**
     * public static case(Static field of Parent)
     * Parent.staticSet
     */
    @Test
    public void case2() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "staticSet", false);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        mockQContextParent.push(new DataValue(new MetaClass(Parent.class)));
        getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        ((LeftValue)mockQContextParent.getValue()).set("staticSet1", errorReporter);
        Assert.assertEquals((mockQContextParent.getValue()).get(), "staticSet1");
    }
    
    /**
     * private static case(Static field of Parent)
     * Parent.staticSetPrivate notSetAble
     */
    @Test
    public void case3() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "staticSetPrivate", false);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        mockQContextParent.push(new DataValue(new MetaClass(Parent.class)));
        try {
            getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        }
        catch (QLRuntimeException e) {
            Assert.assertEquals("FIELD_NOT_FOUND", e.getErrorCode());
            return;
        }
        Assert.fail();
    }
    
    /**
     * private static case(Static field of Parent)
     * Parent.staticSetPrivate allowPrivateAccess
     */
    @Test
    public void case4() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "staticSetPrivate", false);
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        mockQContextParent.push(new DataValue(new MetaClass(Parent.class)));
        getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals((mockQContextParent.getValue()).get(), "staticSetPrivate");
    }
    
    /**
     * static case(Static method of Parent instance)
     * parent.getStaticGet() allowPrivateAccess
     */
    @Test
    public void case5() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "staticGet", false);
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        mockQContextParent.push(new Parent());
        getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals((mockQContextParent.getValue()).get(), "staticGet1");
    }
    
    /**
     * normal case(member method of Parent instance)
     * parent.getAge()
     */
    @Test
    public void case6() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "age", false);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        mockQContextParent.push(new Parent());
        getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        ((LeftValue)mockQContextParent.getValue()).set(35, errorReporter);
        Assert.assertEquals((mockQContextParent.getValue()).get(), 35);
    }
    
    /**
     * normal case(member field of Parent instance)
     * parent.name allowPrivateAccess
     */
    @Test
    public void case7() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "name", false);
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        mockQContextParent.push(new Parent());
        getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        ((LeftValue)mockQContextParent.getValue()).set("name1", errorReporter);
        Assert.assertEquals((mockQContextParent.getValue()).get(), "name1");
    }
    
    /**
     * error case(member field of Parent instance)
     * parent.name not accessible
     */
    @Test
    public void case8() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "name", false);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        mockQContextParent.push(new Parent());
        try {
            getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
            Assert.fail();
        }
        catch (QLRuntimeException e) {
            Assert.assertEquals("FIELD_NOT_FOUND", e.getErrorCode());
        }
    }
    
    /**
     * normal case(normal method of Parent,Child instance)
     * child.getAge() instead of parent.getAge()
     */
    @Test
    public void case9() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "age", false);
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        mockQContextParent.push(new Child());
        getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals((mockQContextParent.getValue()).get(), 11);
    }
    
    /**
     * normal case(member method of Parent,Child instance)
     * parent.getBirth() public instead of child.birth(not allow)
     */
    @Test
    public void case10() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "birth", false);
        MockQContextParent mockQContextParent = new MockQContextParent(false);
        mockQContextParent.push(new Child());
        getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals((mockQContextParent.getValue()).get(), "2022-01-01");
    }
    
    /**
     * error case(member method of Parent instance)
     * parent.getMethod1() not found
     */
    @Test
    public void case11() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "method1", false);
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        mockQContextParent.push(new Child());
        try {
            getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
            Assert.fail();
        }
        catch (QLRuntimeException e) {
            Assert.assertEquals("FIELD_NOT_FOUND", e.getErrorCode());
        }
    }
    
    /**
     * enum attr get
     * TestEnum.skt.getValue()
     */
    @Test
    public void case12() {
        ErrorReporter errorReporter = new MockErrorReporter();
        GetFieldInstruction getFieldInstruction = new GetFieldInstruction(errorReporter, "SKT", false);
        MockQContextParent mockQContextParent = new MockQContextParent(true);
        mockQContextParent.push(new DataValue(new MetaClass(TestEnum.class)));
        getFieldInstruction.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        GetFieldInstruction getFieldInstruction1 = new GetFieldInstruction(errorReporter, "value", false);
        getFieldInstruction1.execute(mockQContextParent, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals((mockQContextParent.getValue()).get(), -1);
    }
    
}
