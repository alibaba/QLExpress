package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.exception.QLTransferException;
import com.alibaba.qlexpress4.utils.CacheUtil;

import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:39
 */
public class StringConversion {
    public static String trans(Object object) {
        if (object == null) {
            return "null";
        }
        Class<?> beanClass;
        String methodName = "toString";

        if (object instanceof Class) {
            beanClass = (Class<?>) object;
        } else {
            beanClass = object.getClass();
        }
        try {
            Method method = beanClass.getMethod(methodName, null);
            return method.invoke(object, null).toString();
        } catch (Exception e) {
            throw new QLTransferException("can not cast " + object.getClass().getName()
                    + " value " + object + " to String type");
        }
    }
}
