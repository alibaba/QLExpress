package com.alibaba.qlexpress4.generic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class GenericTypeTest {
    @Test
    public void test()
        throws NoSuchFieldException, NoSuchMethodException {
        // 获取属性的泛型类型
        Field field1 = Class4GenericType.class.getDeclaredField("field1");
        Type type1 = field1.getGenericType();
        Type[] actualTypeArguments = ((ParameterizedType)type1).getActualTypeArguments();
        Assert.assertEquals(String.class, actualTypeArguments[0]);
        
        Field field2 = Class4GenericType.class.getDeclaredField("field2");
        Type type2 = field2.getGenericType();
        Assert.assertEquals(String.class, type2);
        
        // 获取方法出入参的泛型类型
        Method method1 = Class4GenericType.class.getMethod("method1", List.class);
        Parameter firstParameter = method1.getParameters()[0];
        ParameterizedType firstParameterizedType = (ParameterizedType)(firstParameter.getParameterizedType());
        Assert.assertEquals(Long.class, firstParameterizedType.getActualTypeArguments()[0]);
        
        Type returnType = method1.getGenericReturnType();
        ParameterizedType returnParameterizedType = (ParameterizedType)returnType;
        Assert.assertEquals(String.class, returnParameterizedType.getActualTypeArguments()[0]);
    }
    
    public static class Class4GenericType {
        private List<String> field1;
        
        private String field2;
        
        public List<String> method1(List<Long> longList) {
            return Collections.emptyList();
        }
    }
}
