package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitConstructor;
import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitMatcher;
import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitMethod;
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
        public static QLImplicitConstructor findConstructorMostSpecificSignature( Class<?> baseClass, Class<?>[] types) {
            List<Constructor<?>> constructorList = new ArrayList();
            List<Class<?>[]> listClass = new ArrayList();
            for (Constructor<?> constructor : baseClass.getConstructors()) {
                Class<?>[] constructorParameters = constructor.getParameterTypes();
                if (constructorParameters.length == types.length) {
                    listClass.add(constructorParameters);
                    constructorList.add(constructor);
                }
            }
            QLImplicitMatcher matcher = MemberHandler.Preferred.findMostSpecificSignatureForConstructor(types, listClass.toArray(new Class[0][]));
            return matcher.getMatchIndex() == BasicUtil.DEFAULT_MATCH_INDEX ? null :  new QLImplicitConstructor(constructorList.get(matcher.getMatchIndex()),matcher.needImplicitTrans());
        }
    }
}
