package com.alibaba.qlexpress4.runtime.function;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Author: DQinYuan
 */
public class FilterExtensionFunction extends ExtensionFunction {
    
    public static FilterExtensionFunction INSTANCE = new FilterExtensionFunction();
    
    private FilterExtensionFunction() {
    }
    
    @Override
    public Class<?>[] getParameterTypes() {
        return new Class[] {Predicate.class};
    }
    
    @Override
    public String getName() {
        return "filter";
    }
    
    @Override
    public Class<?> getDeclaringClass() {
        return List.class;
    }
    
    @Override
    public Object invoke(Object obj, Object[] args)
        throws InvocationTargetException, IllegalAccessException {
        if (!(obj instanceof List)) {
            return null;
        }
        return ((List)obj).stream().filter((Predicate)args[0]).collect(Collectors.toList());
    }
}
