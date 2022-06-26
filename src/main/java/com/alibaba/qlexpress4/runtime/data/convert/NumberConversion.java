package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.exception.QLTransferException;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:41
 */
public class NumberConversion {
    public static Object trans(Object object, Class type) {
        if (Number.class.isAssignableFrom(type)) {
            Number n = castToNumber(object, type);
            if (type == Byte.class || type == byte.class) {
                return n.byteValue();
            }
            if (type == Character.class || type == char.class) {
                return (char) n.intValue();
            }
            if (type == Short.class || type == short.class) {
                return n.shortValue();
            }
            if (type == Integer.class || type == int.class) {
                return n.intValue();
            }
            if (type == Long.class || type == long.class) {
                return n.longValue();
            }
            if (type == Float.class || type == float.class) {
                return n.floatValue();
            }
            if (type == Double.class || type == double.class) {
                double answer = n.doubleValue();
                if (!(n instanceof Double) && (answer == Double.NEGATIVE_INFINITY
                        || answer == Double.POSITIVE_INFINITY)) {
                    throw new QLTransferException("can not cast " + object.getClass().getName()
                            + " value " + object + " to double type");
                }
                return answer;
            }
            if (type == BigDecimal.class) {
                return NumberMath.toBigDecimal(n);
            }
            if (type == BigInteger.class) {
                return NumberMath.toBigInteger(n);
            }
        }

        throw new QLTransferException("can not cast " + object.getClass().getName()
                + " value " + object + " to number type");
    }


    public static Number castToNumber(Object object, Class type) {
        if (object instanceof Number) {
            return (Number) object;
        }
        if (object instanceof Character) {
            return (int) (Character) object;
        }
        if (object instanceof String) {
            String c = (String) object;
            if (c.length() == 1) {
                return (int) c.charAt(0);
            } else {
                throw new QLTransferException("can not cast " + object.getClass().getName()
                        + " value " + object + " to String type");
            }
        }
        throw new QLTransferException("can not cast " + object.getClass().getName()
                + " value " + object + " to number type");
    }

}
