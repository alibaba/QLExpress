package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.utils.BasicUtils;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午5:20
 */
public class MethodCacheElement implements ICacheElement {
    private static final ICache<String, Object> METHOD_CACHE = CacheFactory.cacheBuilder(128);

    @Override
    public Object buildCacheKey(Class<?> baseClass, String propertyName, Class<?>[] types) {
        StringBuilder builder = new StringBuilder();
        builder.append(baseClass.getName())
                .append("#")
                .append(propertyName)
                .append(".");

        if(types == null){
            return builder.toString();
        }

        for(Class clazz: types){
            if (clazz == null) {
                builder.append(BasicUtils.NULL_SIGN);
            } else {
                builder.append(clazz.getName());
            }
            builder.append(",");
        }

        String result = builder.toString();
        return result.substring(0,result.length()-1);
    }

    @Override
    public Member getCacheElement(String key, Class<?> baseClass, String methodName, Class<?>[] types, boolean publicOnly, boolean isStatic) {
        Object result = METHOD_CACHE.get(key);
        if (result == null) {
            result = getElement(baseClass, methodName, types, publicOnly, isStatic);
            if (result == null) {
                METHOD_CACHE.put(key, void.class);
            } else {
                ((Method)result).setAccessible(true);
                METHOD_CACHE.put(key, result);
            }
        } else if (result == void.class) {
            result = null;
        }
        return (Method)result;
    }


    @Override
    public Member getElement(Class<?> baseClass, String methodName, Class<?>[] types, boolean publicOnly, boolean isStatic) {
        List<Method> candidates = MethodHandler.Preferred.gatherMethodsRecursive
                (baseClass, methodName, true, types.length, publicOnly, isStatic, null);
        return MethodHandler.Preferred.findMostSpecificMethod(types, candidates.toArray(new Method[0]));
    }


}
