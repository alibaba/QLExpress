package com.alibaba.qlexpress4.runtime.data.convert;
import com.alibaba.qlexpress4.proxy.QLambdaInvocationHandler;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;
import com.alibaba.qlexpress4.utils.CacheUtil;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Author: TaoKan
 */
public class ObjTypeConvertor {

    public static QConverted cast(Object value, Class<?> type) {
        // no need to convert
        if (noNeedConvert(value, type)) {
            return converted(value);
        }
        if (type == Character.class || type == char.class) {
            return castChar(value);
        }
        if (CacheUtil.isFunctionInterface(type)) {
           return castFunctionInter(value, type);
        }
        // unboxed boxed
        if (type == boolean.class || type == Boolean.class) {
            if (value instanceof Boolean) {
                return converted(value);
            }
            return unConvertible();
        }
        if (type == byte.class || type == Byte.class) {
            if (value instanceof Byte) {
                return converted(value);
            }
            if (value instanceof Number) {
                return converted(((Number) value).byteValue());
            }
            return unConvertible();
        }
        if (type == short.class || type == Short.class) {
            if (value instanceof Short) {
                return converted(value);
            }
            if (value instanceof Number) {
                return converted(((Number) value).shortValue());
            }
            return unConvertible();
        }
        if (type == int.class || type == Integer.class) {
            if (value instanceof Integer) {
                return converted(value);
            }
            if (value instanceof Number) {
                return converted(((Number) value).intValue());
            }
            return unConvertible();
        }
        if (type == long.class || type == Long.class) {
            if (value instanceof Long) {
                return converted(value);
            }
            if (value instanceof Number) {
                return converted(((Number) value).longValue());
            }
            return unConvertible();
        }
        if (type == float.class || type == Float.class) {
            if (value instanceof Float) {
                return converted(value);
            }
            if (value instanceof Number) {
                return converted(((Number) value).floatValue());
            }
            return unConvertible();
        }
        if (type == double.class || type == Double.class) {
            if (value instanceof Double) {
                return converted(value);
            }
            if (value instanceof Number) {
                return converted(((Number) value).doubleValue());
            }
            return unConvertible();
        }
        if (type == BigInteger.class) {
            if (value instanceof Number) {
                return converted(NumberMath.toBigInteger((Number) value));
            }
            return unConvertible();
        }
        if (type == BigDecimal.class) {
            if (value instanceof Number) {
                return converted(NumberMath.toBigDecimal((Number) value));
            }
            return unConvertible();
        }
        return unConvertible();
    }

    private static QConverted castFunctionInter(Object value, Class<?> functionInter) {
        if (value instanceof QLambda) {
            return converted(Proxy.newProxyInstance(functionInter.getClassLoader(),
                    new Class<?>[]{functionInter}, new QLambdaInvocationHandler((QLambda) value)));
        }
        return unConvertible();
    }

    private static QConverted castChar(Object value) {
        if (value instanceof Character) {
            return converted(value);
        }
        if (value instanceof Number) {
            return converted((char) ((Number) value).intValue());
        }
        if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.length() == 1) {
                return converted(strValue.charAt(0));
            }
            return unConvertible();
        }
        return unConvertible();
    }

    private static boolean noNeedConvert(Object value, Class<?> type) {
        return value == null || type.isInstance(value);
    }

    private static QConverted converted(Object converted) {
        return new QConverted(true, converted);
    }

    private static QConverted unConvertible() {
        return new QConverted(false, null);
    }

    public static class QConverted {

        private final boolean convertible;

        private final Object converted;

        public QConverted(boolean convertible, Object converted) {
            this.convertible = convertible;
            this.converted = converted;
        }

        public boolean isConvertible() {
            return convertible;
        }

        public Object getConverted() {
            return converted;
        }
    }

}
