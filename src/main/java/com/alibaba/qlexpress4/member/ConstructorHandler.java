package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.runtime.data.implicit.ConstructorReflect;
import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitMatcher;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: TaoKan
 */
public class ConstructorHandler extends MemberHandler {
    public static class Preferred {
        public static ConstructorReflect findConstructorMostSpecificSignature(Class<?> baseClass, Class<?>[] types,
                                                                              boolean allowAccessPrivate) {
            List<Constructor<?>> constructorList = new ArrayList<>();
            List<Class<?>[]> listClass = new ArrayList<>();
            for (Constructor<?> constructor : baseClass.getConstructors()) {
                if (!allowAccessPrivate && !BasicUtil.isPublic(constructor)) {
                    continue;
                }
                Class<?>[] constructorParameters = constructor.getParameterTypes();
                if (constructorParameters.length == types.length) {
                    listClass.add(constructorParameters);
                    constructorList.add(constructor);
                }else {
                    if(constructorParameters.length > 0){
                        Class<?> endClazz = constructorParameters[constructorParameters.length - 1];
                        if(endClazz.isArray()){
                            listClass.add(constructorParameters);
                            constructorList.add(constructor);
                        }
                    }
                }
            }
            QLImplicitMatcher matcher = MemberHandler.Preferred.findMostSpecificSignatureForConstructor(types, listClass.toArray(new Class[0][]));
            return matcher.getMatchIndex() == BasicUtil.DEFAULT_MATCH_INDEX ? null :
                    new ConstructorReflect(constructorList.get(matcher.getMatchIndex()),matcher.needImplicitTrans(), matcher.getVars());
        }
    }
}
