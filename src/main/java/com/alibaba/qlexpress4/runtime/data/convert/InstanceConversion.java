package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.proxy.QLambdaInvocationHandler;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.utils.CacheUtil;

import java.lang.reflect.Proxy;

/**
 * @Author TaoKan
 * @Date 2022/6/25 下午8:49
 */
public class InstanceConversion {

    public static Object castObject(Object value, Class<?> type) {
        if (value == null) {
            return type == boolean.class ? Boolean.FALSE : null;
        }
        if (type == Object.class) {
            return value;
        }
        final Class clazz = value.getClass();
        if (type == clazz || type.isAssignableFrom(clazz)) {
            return value;
        } else if (type.isArray()) {
            return ArrayConversion.trans(value, type);
        } else if (type.isEnum()) {
            return EnumConversion.trans(value, (Class<? extends Enum>) type);
        } else if (type == String.class) {
            return StringConversion.trans(value);
        } else if (type == Character.class || type == char.class) {
            return CharacterConversion.trans(value);
        } else if (type == Boolean.class || type == boolean.class) {
            return BooleanConversion.trans(value);
        } else if (type == Class.class) {
            return ClassConversion.trans(value);
        } else if (QLambda.class.isAssignableFrom(value.getClass()) && CacheUtil.isFunctionInterface(type)) {
            return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, new QLambdaInvocationHandler((QLambda) value));
        }
        return NumberConversion.trans(value, type);
    }


}
