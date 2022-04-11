package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.utils.PropertiesUtils;
import org.junit.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2022/4/9 上午10:51
 */


public class TestPropertyUtils {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        Parent parent = new Parent();
        parent.setAge(35);
        // getPropertyValue private field - non get
        Assert.assertNull(PropertiesUtils.getPropertyValue(parent,"name",false));
        Assert.assertNull(PropertiesUtils.getPropertyType(parent,"name"));
        Assert.assertNull(PropertiesUtils.getClzField(Parent.class,"name",false));
        Assert.assertTrue(PropertiesUtils.getPropertyValue(Parent.class,"staticPublic",false).equals("staticPublic"));
        Assert.assertTrue(PropertiesUtils.getClzField(Parent.class,"staticPublic",false).equals("staticPublic"));
        Assert.assertNull(PropertiesUtils.getClzField(Parent.class,"staticPrivate",false));
        Assert.assertTrue(PropertiesUtils.getClzField(Parent.class,"staticGet",false).equals("staticGet"));
        // getPropertyValue private field - public get
        Assert.assertTrue((int)PropertiesUtils.getPropertyValue(parent,"age",false) == 35);
        Assert.assertTrue(PropertiesUtils.getPropertyType(parent,"age") == int.class);
        // getPropertyValue public field
        Assert.assertTrue(PropertiesUtils.getPropertyValue(parent,"sex",false).equals("man"));
        Assert.assertTrue(PropertiesUtils.getPropertyType(parent,"sex").equals(String.class));
        Assert.assertTrue(PropertiesUtils.getPropertyType(Parent.class,"staticPublic").equals(java.lang.String.class));
        PropertiesUtils.setPropertyValue(parent,"age",15,false);
        Assert.assertTrue((int)PropertiesUtils.getPropertyValue(parent,"age",false) == 15);

        List<Method> method1 = PropertiesUtils.getMethod(parent,"getWork");
        Assert.assertTrue(method1.size() == 1);

        Parent pc = new Child();
        pc.setAge(35);
        Assert.assertTrue((int)PropertiesUtils.getPropertyValue(pc,"age",false) == 35);
        Assert.assertTrue(PropertiesUtils.getPropertyType(pc,"age") == int.class);
        List<Method> method2 = PropertiesUtils.getMethod(pc,"getWork");
        Assert.assertTrue(method2.size() == 2);


        Child c = new Child();
        c.setAge(35);
        Assert.assertTrue(PropertiesUtils.getPropertyValue(c,"work",false).equals("child"));
        Assert.assertTrue((int)PropertiesUtils.getPropertyValue(c,"age",false) == 35);
        Assert.assertTrue(PropertiesUtils.getPropertyValue(c,"sex",false).equals("man"));
        Assert.assertTrue(PropertiesUtils.getPropertyType(c,"age") == int.class);
        List<Method> method3 = PropertiesUtils.getMethod(c,"getWork");
        Assert.assertTrue(method3.size() == 2);

        List<Method> method4 = PropertiesUtils.getClzMethod(Child.class,"findStatic");
        Assert.assertTrue(method4.size() == 1 && method4.get(0).getDeclaringClass().equals(com.alibaba.qlexpress4.test.property.Parent.class));

        PropertiesUtils.setPropertyValue(Parent.class,"staticSet","st1",false);
        Assert.assertTrue(Parent.staticSet.equals("st1"));


        Map<String,String> map = new HashMap<>();
        PropertiesUtils.setPropertyValue(map,"key","value",false);
        Assert.assertTrue(PropertiesUtils.getPropertyValue(map,"key",false).equals("value"));
        Assert.assertTrue(PropertiesUtils.getPropertyType(map,"key").equals(java.lang.String.class));

        String[] aa = {"11"};
        Assert.assertTrue(PropertiesUtils.getPropertyValue(aa,"length",false).equals(1));
        Assert.assertTrue(PropertiesUtils.getPropertyType(aa,"length").equals(int.class));

        Assert.assertTrue(PropertiesUtils.getPropertyValue(c,"booValue",false).equals(true));

    }
}
