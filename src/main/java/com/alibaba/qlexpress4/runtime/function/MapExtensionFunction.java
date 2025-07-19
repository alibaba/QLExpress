package com.alibaba.qlexpress4.runtime.function;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: DQinYuan
 */
public class MapExtensionFunction extends ExtensionFunction {
    
    public static final MapExtensionFunction INSTANCE = new MapExtensionFunction();
    
    private MapExtensionFunction() {
    }
    
    @Override
    public Class<?>[] getParameterTypes() {
        return new Class[] {Function.class};
    }
    
    @Override
    public String getName() {
        return "map";
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
        return ((List)obj).stream().map((Function)args[0]).collect(Collectors.toList());
    }
}
