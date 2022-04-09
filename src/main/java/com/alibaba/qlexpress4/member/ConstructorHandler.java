package com.alibaba.qlexpress4.member;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午6:06
 */
public class ConstructorHandler extends MemberHandler {
    public static class Preferred{
        public static Constructor<?> findConstructorMostSpecificSignature(Class<?> baseClass, String name, Class<?>[] types, boolean publicOnly, boolean isStatic) {
            List<Constructor<?>> constructorList = new ArrayList();
            List<Class<?>[]> listClass = new ArrayList();
            for (Constructor<?> constructor : baseClass.getConstructors()) {
                if (constructor.getParameterTypes().length == types.length) {
                    listClass.add(constructor.getParameterTypes());
                    constructorList.add(constructor);
                }
            }
            int match = MemberHandler.Preferred.findMostSpecificSignature(types, listClass.toArray(new Class[0][]));
            return match == -1 ? null : constructorList.get(match);
        }
    }
}
