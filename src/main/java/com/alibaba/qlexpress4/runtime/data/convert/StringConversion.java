package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:39
 */
public class StringConversion {
    public static QLConvertResult trans(Object object) {
        if (object == null) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, BasicUtil.NULL_SIGN);
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
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, method.invoke(object, null).toString());
        } catch (Exception e) {
            return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);
        }
    }
}
