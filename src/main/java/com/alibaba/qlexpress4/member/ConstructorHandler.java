package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午6:06
 */
public class ConstructorHandler extends MemberHandler {
    public static class Preferred {
        public static Constructor<?> findConstructorMostSpecificSignature(QLCaches qlCaches, Class<?> baseClass, Class<?>[] types) {
            List<Constructor<?>> constructorList = new ArrayList();
            List<Class<?>[]> listClass = new ArrayList();
            for (Constructor<?> constructor : baseClass.getConstructors()) {
                Class<?>[] constructorParameters = constructor.getParameterTypes();
                if (constructorParameters.length == types.length) {
                    listClass.add(constructorParameters);
                    constructorList.add(constructor);
                }
            }
            int match = MemberHandler.Preferred.findMostSpecificSignatureForConstructor(qlCaches, types, listClass.toArray(new Class[0][]));
            return match == BasicUtil.DEFAULT_MATCH_INDEX ? null : constructorList.get(match);
        }
    }
}
