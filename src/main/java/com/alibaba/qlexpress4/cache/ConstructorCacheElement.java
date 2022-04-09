package com.alibaba.qlexpress4.cache;

import com.alibaba.qlexpress4.member.MemberHandler;
import com.alibaba.qlexpress4.utils.BasicUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午5:20
 */
public class ConstructorCacheElement implements ICacheElement {
    private static final ICache<String, Object> CONSTR_CACHE = CacheFactory.cacheBuilder(128);


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
        Object result = CONSTR_CACHE.get(key);
        if (result == null) {
            result = getElement(baseClass, methodName, types, publicOnly, isStatic);
            CONSTR_CACHE.put(key, result);
        }
        return (Constructor<?>)result;
    }


    @Override
    public Constructor<?> getElement(Class<?> baseClass, String name, Class<?>[] types, boolean publicOnly, boolean isStatic) {
            Constructor<?>[] constructors = baseClass.getConstructors();
            List<Constructor<?>> constructorList = new ArrayList<>();
            List<Class<?>[]> listClass = new ArrayList<>();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterTypes().length == types.length) {
                    listClass.add(constructor.getParameterTypes());
                    constructorList.add(constructor);
                }
            }
            int match = MemberHandler.Preferred.findMostSpecificSignature(types, listClass.toArray(new Class[0][]));
            return match == -1 ? null : constructorList.get(match);
    }

}
