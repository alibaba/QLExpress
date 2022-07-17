package com.alibaba.qlexpress4.runtime.data.convert;


import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:41
 */
public class NumberConversion {
    public static QLConvertResult trans(Object object, Class type) {
        if (Number.class.isAssignableFrom(type) || type.isPrimitive()) {
            QLConvertResult castToNumber = castToNumber(object);
            if(castToNumber.getResultType().equals(QLConvertResultType.NOT_TRANS)){
                return castToNumber;
            }

            Number n = (Number) castToNumber.getCastValue();
            if (type == Byte.class || type == byte.class) {
                return new QLConvertResult(QLConvertResultType.CAN_TRANS, n.byteValue());
            }
            if (type == Character.class || type == char.class) {
                return new QLConvertResult(QLConvertResultType.CAN_TRANS, (char) n.intValue());
            }
            if (type == Short.class || type == short.class) {
                return new QLConvertResult(QLConvertResultType.CAN_TRANS, n.shortValue());
            }
            if (type == Integer.class || type == int.class) {
                return new QLConvertResult(QLConvertResultType.CAN_TRANS, n.intValue());
            }
            if (type == Long.class || type == long.class) {
                return new QLConvertResult(QLConvertResultType.CAN_TRANS, n.longValue());
            }
            if (type == Float.class || type == float.class) {
                return new QLConvertResult(QLConvertResultType.CAN_TRANS, n.floatValue());
            }
            if (type == Double.class || type == double.class) {
                double answer = n.doubleValue();
                if (!(n instanceof Double) && (answer == Double.NEGATIVE_INFINITY
                        || answer == Double.POSITIVE_INFINITY)) {
                   return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);
                }
                return new QLConvertResult(QLConvertResultType.CAN_TRANS, answer);
            }
            return ExtendConversion.extendNumberConvert(n, type);
        }
        return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);
    }


    public static QLConvertResult castToNumber(Object object) {
        if (object instanceof Number) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, (Number) object);
        }
        if (object instanceof Character) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, (int) (Character) object);
        }
        return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);

    }

}
