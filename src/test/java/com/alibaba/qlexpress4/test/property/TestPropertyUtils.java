package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.utils.CacheUtil;
import com.alibaba.qlexpress4.utils.PropertiesUtil;
import org.junit.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/4/9 上午10:51
 */


public class TestPropertyUtils {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        CacheUtil.initCache(128, false);

        Parent parent = new Parent();
        parent.setAge(35);
        // getPropertyValue private field - non get
        Assert.assertNull(PropertiesUtil.getPropertyValue(parent, "name", false));
        Assert.assertNull(PropertiesUtil.getPropertyType(parent, "name"));
        Assert.assertNull(PropertiesUtil.getClzField(Parent.class, "name", false));
        Assert.assertTrue(PropertiesUtil.getClzField(Parent.class, "staticPublic", false).equals("staticPublic"));
        Assert.assertNull(PropertiesUtil.getClzField(Parent.class, "staticPrivate", false));
        Assert.assertTrue(PropertiesUtil.getClzField(Parent.class, "staticGet", false).equals("staticGet"));
        // getPropertyValue private field - public get
        Assert.assertTrue((int) PropertiesUtil.getPropertyValue(parent, "age", false) == 35);
        Assert.assertTrue(PropertiesUtil.getPropertyType(parent, "age") == int.class);
        // getPropertyValue public field
        Assert.assertTrue(PropertiesUtil.getPropertyValue(parent, "sex", false).equals("man"));
        Assert.assertTrue(PropertiesUtil.getPropertyValue(parent, "生日", false).equals("2022-01-01"));

        Assert.assertTrue(PropertiesUtil.getPropertyType(parent, "sex").equals(String.class));
        Assert.assertTrue(PropertiesUtil.getPropertyType(Parent.class, "staticPublic").equals(java.lang.String.class));
        PropertiesUtil.setPropertyValue(parent, "age", 15, false);
        Assert.assertTrue((int) PropertiesUtil.getPropertyValue(parent, "age", false) == 15);

        List<Method> method1 = PropertiesUtil.getMethod(parent, "getWork", false);
        Assert.assertTrue(method1.size() == 1);

        Parent pc = new Child();
        pc.setAge(35);
        Assert.assertTrue((int) PropertiesUtil.getPropertyValue(pc, "age", false) == 35);
        Assert.assertTrue(PropertiesUtil.getPropertyType(pc, "age") == int.class);

        Child c = new Child();
        c.setAge(35);
        Assert.assertTrue(PropertiesUtil.getPropertyValue(c, "work", false).equals("child"));
        Assert.assertTrue((int) PropertiesUtil.getPropertyValue(c, "age", false) == 35);
        Assert.assertTrue(PropertiesUtil.getPropertyValue(c, "sex", false).equals("man"));
        Assert.assertTrue(PropertiesUtil.getPropertyType(c, "age") == int.class);
        List<Method> method3 = PropertiesUtil.getMethod(c, "getWork", false);
        Assert.assertTrue(method3.size() == 2);

        List<Method> method4 = PropertiesUtil.getClzMethod(Child.class, "findStatic");
        Assert.assertTrue(method4.size() == 1 && method4.get(0).getDeclaringClass().equals(com.alibaba.qlexpress4.test.property.Parent.class));

        PropertiesUtil.setClzPropertyValue(Parent.class, "staticSet", "st1", false);
        Assert.assertTrue(Parent.staticSet.equals("st1"));

        Assert.assertTrue(PropertiesUtil.getPropertyValue(c, "booValue", false).equals(true));
    }
}
