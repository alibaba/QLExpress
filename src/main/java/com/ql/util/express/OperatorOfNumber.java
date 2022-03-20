package com.ql.util.express;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.ql.util.express.exception.QLException;

/**
 * 数字运行函数集合
 *
 * @author qhlhl2010@gmail.com
 */

interface NumberType {
    int NUMBER_TYPE_BYTE = 1;
    int NUMBER_TYPE_SHORT = 2;
    int NUMBER_TYPE_INT = 3;
    int NUMBER_TYPE_LONG = 4;
    int NUMBER_TYPE_FLOAT = 5;
    int NUMBER_TYPE_DOUBLE = 6;
    int NUMBER_TYPE_BIG_DECIMAL = 7;
}

public class OperatorOfNumber {
    public static final BigDecimal BIG_DECIMAL_LONG_MAX = new BigDecimal(Long.MAX_VALUE);
    public static final BigDecimal BIG_DECIMAL_LONG_MIN = new BigDecimal(Long.MIN_VALUE);
    public static final BigDecimal BIG_DECIMAL_INTEGER_MAX = new BigDecimal(Integer.MAX_VALUE);
    public static final BigDecimal BIG_DECIMAL_INTEGER_MIN = new BigDecimal(Integer.MIN_VALUE);

    //BIG_DECIMAL_COMPARE_LESS
    public static final Integer LESS = -1;
    //BIG_DECIMAL_COMPARE_MORE
    public static final Integer MORE = 1;


    private OperatorOfNumber() {
        throw new IllegalStateException("Utility class");
    }

    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal bigDecimal = BigDecimal.valueOf(v);
        BigDecimal oneBigDecimal = new BigDecimal("1");
        return bigDecimal.divide(oneBigDecimal, scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 获取数据类型精度顺序
     *
     * @param clazz
     * @return
     */
    public static int getSeq(Class<?> clazz) {
        if (clazz == Byte.class || clazz == byte.class) {
            return NumberType.NUMBER_TYPE_BYTE;
        }
        if (clazz == Short.class || clazz == short.class) {
            return NumberType.NUMBER_TYPE_SHORT;
        }
        if (clazz == Integer.class || clazz == int.class) {
            return NumberType.NUMBER_TYPE_INT;
        }
        if (clazz == Long.class || clazz == long.class) {
            return NumberType.NUMBER_TYPE_LONG;
        }
        if (clazz == Float.class || clazz == float.class) {
            return NumberType.NUMBER_TYPE_FLOAT;
        }
        if (clazz == Double.class || clazz == double.class) {
            return NumberType.NUMBER_TYPE_DOUBLE;
        }
        if (clazz == BigDecimal.class) {
            return NumberType.NUMBER_TYPE_BIG_DECIMAL;
        }
        throw new RuntimeException("不能处理的数据类型：" + clazz.getName());
    }

    /**
     * 进行数据类型转换
     *
     * @param value
     * @param type
     * @return
     */
    public static Number transfer(Number value, Class<?> type, boolean isForce) {
        if (isForce || !(value instanceof BigDecimal)) {
            if (type.equals(byte.class) || type.equals(Byte.class)) {
                return value.byteValue();
            } else if (type.equals(short.class) || type.equals(Short.class)) {
                return value.shortValue();
            } else if (type.equals(int.class) || type.equals(Integer.class)) {
                return value.intValue();
            } else if (type.equals(long.class) || type.equals(Long.class)) {
                return value.longValue();
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                return value.floatValue();
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                return value.doubleValue();
            } else if (type.equals(BigDecimal.class)) {
                return new BigDecimal(value.toString());
            } else {
                throw new RuntimeException("没有处理的数据类型：" + type.getName());
            }
        } else {
            if (type.equals(byte.class) || type.equals(Byte.class)) {
                if (((BigDecimal)value).scale() > 0) {
                    throw new RuntimeException("有小数位，不能转化为：" + type.getName());
                }
                return value.byteValue();
            } else if (type.equals(short.class) || type.equals(Short.class)) {
                if (((BigDecimal)value).scale() > 0) {
                    throw new RuntimeException("有小数位，不能转化为：" + type.getName());
                }
                return value.shortValue();
            } else if (type.equals(int.class) || type.equals(Integer.class)) {
                if (((BigDecimal)value).scale() > 0) {
                    throw new RuntimeException("有小数位，不能转化为：" + type.getName());
                }
                return value.intValue();
            } else if (type.equals(long.class) || type.equals(Long.class)) {
                if (((BigDecimal)value).scale() > 0) {
                    throw new RuntimeException("有小数位，不能转化为：" + type.getName());
                }
                return value.longValue();
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                return value.floatValue();
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                return value.doubleValue();
            } else {
                throw new RuntimeException("没有处理的数据类型：" + type.getName());
            }
        }
    }

    public static int compareNumber(Number op1, Number op2) {
        int type1 = OperatorOfNumber.getSeq(op1.getClass());
        int type2 = OperatorOfNumber.getSeq(op2.getClass());
        int type = Math.max(type1, type2);
        if (type == 1) {
            byte o1 = op1.byteValue();
            byte o2 = op2.byteValue();
            return Byte.compare(o1, o2);
        }
        if (type == 2) {
            short o1 = op1.shortValue();
            short o2 = op2.shortValue();
            return Short.compare(o1, o2);
        }
        if (type == 3) {
            int o1 = op1.intValue();
            int o2 = op2.intValue();
            return Integer.compare(o1, o2);
        }
        if (type == 4) {
            long o1 = op1.longValue();
            long o2 = op2.longValue();
            return Long.compare(o1, o2);
        }
        if (type == 5) {
            float o1 = op1.floatValue();
            float o2 = op2.floatValue();
            return Float.compare(o1, o2);
        }
        if (type == 6) {
            double o1 = op1.doubleValue();
            double o2 = op2.doubleValue();
            return Double.compare(o1, o2);
        }
        if (type == 7) {
            BigDecimal o1 = new BigDecimal(op1.toString());
            BigDecimal o2 = new BigDecimal(op2.toString());
            return o1.compareTo(o2);
        }
        throw new RuntimeException("比较操作错误:op1=" + op1 + ",op2=" + op2);
    }

    public static Object add(Object op1, Object op2, boolean isPrecise) throws Exception {
        if (op1 == null) {
            op1 = "null";
        }
        if (op2 == null) {
            op2 = "null";
        }
        if (op1 instanceof String || op2 instanceof String) {
            return op1.toString() + op2;
        }
        if (isPrecise) {
            return PreciseNumberOperator.addPrecise((Number)op1, (Number)op2);
        } else {
            return NormalNumberOperator.addNormal((Number)op1, (Number)op2);
        }
    }

    public static Number subtract(Object op1, Object op2, boolean isPrecise) throws Exception {
        if (isPrecise) {
            return PreciseNumberOperator.subtractPrecise((Number)op1, (Number)op2);
        } else {
            return NormalNumberOperator.subtractNormal((Number)op1, (Number)op2);
        }
    }

    public static Number multiply(Object op1, Object op2, boolean isPrecise) throws Exception {
        if (isPrecise) {
            return PreciseNumberOperator.multiplyPrecise((Number)op1, (Number)op2);
        } else {
            return NormalNumberOperator.multiplyNormal((Number)op1, (Number)op2);
        }
    }

    public static Number divide(Object op1, Object op2, boolean isPrecise) throws Exception {
        if (isPrecise) {
            return PreciseNumberOperator.dividePrecise((Number)op1, (Number)op2);
        } else {
            return NormalNumberOperator.divideNormal((Number)op1, (Number)op2);
        }
    }

    public static Object modulo(Object op1, Object op2) throws Exception {
        return NormalNumberOperator.moduloNormal((Number)op1, (Number)op2);
    }
}

class NormalNumberOperator {
    private NormalNumberOperator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 普通的加法运算
     *
     * @param op1
     * @param op2
     * @return
     * @throws Exception
     */
    public static Number addNormal(Number op1, Number op2) throws Exception {
        int type1 = OperatorOfNumber.getSeq(op1.getClass());
        int type2 = OperatorOfNumber.getSeq(op2.getClass());
        int type = Math.max(type1, type2);
        if (type == NumberType.NUMBER_TYPE_BYTE) {
            return op1.byteValue() + op2.byteValue();
        }
        if (type == NumberType.NUMBER_TYPE_SHORT) {
            return op1.shortValue() + op2.shortValue();
        }
        if (type == NumberType.NUMBER_TYPE_INT) {
            return op1.intValue() + op2.intValue();
        }
        if (type == NumberType.NUMBER_TYPE_LONG) {
            return op1.longValue() + op2.longValue();
        }
        if (type == NumberType.NUMBER_TYPE_FLOAT) {
            return op1.floatValue() + op2.floatValue();
        }
        if (type == NumberType.NUMBER_TYPE_DOUBLE) {
            return op1.doubleValue() + op2.doubleValue();
        }
        if (type == NumberType.NUMBER_TYPE_BIG_DECIMAL) {
            return new BigDecimal(op1.toString()).add(new BigDecimal(op2.toString()));
        }
        throw new QLException("不支持的对象执行了\"+\"操作");
    }

    public static Number subtractNormal(Number op1, Number op2) throws Exception {
        int type1 = OperatorOfNumber.getSeq(op1.getClass());
        int type2 = OperatorOfNumber.getSeq(op2.getClass());
        int type = Math.max(type1, type2);
        if (type == NumberType.NUMBER_TYPE_BYTE) {
            return op1.byteValue() - op2.byteValue();
        }
        if (type == NumberType.NUMBER_TYPE_SHORT) {
            return op1.shortValue() - op2.shortValue();
        }
        if (type == NumberType.NUMBER_TYPE_INT) {
            return op1.intValue() - op2.intValue();
        }
        if (type == NumberType.NUMBER_TYPE_LONG) {
            return op1.longValue() - op2.longValue();
        }
        if (type == NumberType.NUMBER_TYPE_FLOAT) {
            return op1.floatValue() - op2.floatValue();
        }
        if (type == NumberType.NUMBER_TYPE_DOUBLE) {
            return op1.doubleValue() - op2.doubleValue();
        }
        if (type == NumberType.NUMBER_TYPE_BIG_DECIMAL) {
            return new BigDecimal(op1.toString()).subtract(new BigDecimal(op2.toString()));
        }
        throw new QLException("不支持的对象执行了\"-\"操作");
    }

    public static Number multiplyNormal(Number op1, Number op2) throws Exception {
        int type1 = OperatorOfNumber.getSeq(op1.getClass());
        int type2 = OperatorOfNumber.getSeq(op2.getClass());
        int type = Math.max(type1, type2);
        if (type == NumberType.NUMBER_TYPE_BYTE) {
            return op1.byteValue() * op2.byteValue();
        }
        if (type == NumberType.NUMBER_TYPE_SHORT) {
            return op1.shortValue() * op2.shortValue();
        }
        if (type == NumberType.NUMBER_TYPE_INT) {
            return op1.intValue() * op2.intValue();
        }
        if (type == NumberType.NUMBER_TYPE_LONG) {
            return op1.longValue() * op2.longValue();
        }
        if (type == NumberType.NUMBER_TYPE_FLOAT) {
            return op1.floatValue() * op2.floatValue();
        }
        if (type == NumberType.NUMBER_TYPE_DOUBLE) {
            return op1.doubleValue() * op2.doubleValue();
        }
        if (type == NumberType.NUMBER_TYPE_BIG_DECIMAL) {
            return new BigDecimal(op1.toString()).multiply(new BigDecimal(op2.toString()));
        }
        throw new QLException("不支持的对象执行了\"*\"操作");
    }

    public static Number divideNormal(Number op1, Number op2) throws Exception {
        int type1 = OperatorOfNumber.getSeq(op1.getClass());
        int type2 = OperatorOfNumber.getSeq(op2.getClass());
        int type = Math.max(type1, type2);
        if (type == NumberType.NUMBER_TYPE_BYTE) {
            return op1.byteValue() / op2.byteValue();
        }
        if (type == NumberType.NUMBER_TYPE_SHORT) {
            return op1.shortValue() / op2.shortValue();
        }
        if (type == NumberType.NUMBER_TYPE_INT) {
            return op1.intValue() / op2.intValue();
        }
        if (type == NumberType.NUMBER_TYPE_LONG) {
            return op1.longValue() / op2.longValue();
        }
        if (type == NumberType.NUMBER_TYPE_FLOAT) {
            return op1.floatValue() / op2.floatValue();
        }
        if (type == NumberType.NUMBER_TYPE_DOUBLE) {
            return op1.doubleValue() / op2.doubleValue();
        }
        if (type == NumberType.NUMBER_TYPE_BIG_DECIMAL) {
            return new BigDecimal(op1.toString()).divide(new BigDecimal(op2.toString()), BigDecimal.ROUND_HALF_UP);
        }
        throw new QLException("不支持的对象执行了\"/\"操作");
    }

    public static Number moduloNormal(Number op1, Number op2) throws Exception {
        int type1 = OperatorOfNumber.getSeq(op1.getClass());
        int type2 = OperatorOfNumber.getSeq(op2.getClass());
        int type = Math.max(type1, type2);
        if (type == NumberType.NUMBER_TYPE_BYTE) {
            return op1.byteValue() % op2.byteValue();
        }
        if (type == NumberType.NUMBER_TYPE_SHORT) {
            return op1.shortValue() % op2.shortValue();
        }
        if (type == NumberType.NUMBER_TYPE_INT) {
            return op1.intValue() % op2.intValue();
        }
        if (type == NumberType.NUMBER_TYPE_LONG) {
            return op1.longValue() % op2.longValue();
        }
        throw new QLException("不支持的对象执行了\"mod\"操作");
    }
}

/**
 * 高精度计算
 *
 * @author xuannan
 */
class PreciseNumberOperator {
    public static final int DIVIDE_PRECISION = 10;

    private PreciseNumberOperator() {
        throw new IllegalStateException("Utility class");
    }

    public static Number addPrecise(Number op1, Number op2) {
        BigDecimal result;
        if (op1 instanceof BigDecimal) {
            if (op2 instanceof BigDecimal) {
                result = ((BigDecimal)op1).add((BigDecimal)op2);
            } else {
                result = ((BigDecimal)op1).add(new BigDecimal(op2.toString()));
            }
        } else {
            if (op2 instanceof BigDecimal) {
                result = new BigDecimal(op1.toString()).add((BigDecimal)op2);
            } else {
                result = new BigDecimal(op1.toString()).add(new BigDecimal(op2.toString()));
            }
        }
        return basicNumberFormatTransfer(result);
    }

    public static Number subtractPrecise(Number op1, Number op2) {
        BigDecimal result;
        if (op1 instanceof BigDecimal) {
            if (op2 instanceof BigDecimal) {
                result = ((BigDecimal)op1).subtract((BigDecimal)op2);
            } else {
                result = ((BigDecimal)op1).subtract(new BigDecimal(op2.toString()));
            }
        } else {
            if (op2 instanceof BigDecimal) {
                result = new BigDecimal(op1.toString()).subtract((BigDecimal)op2);
            } else {
                result = new BigDecimal(op1.toString()).subtract(new BigDecimal(op2.toString()));
            }
        }
        return basicNumberFormatTransfer(result);
    }

    public static Number multiplyPrecise(Number op1, Number op2) {
        BigDecimal result;
        if (op1 instanceof BigDecimal) {
            if (op2 instanceof BigDecimal) {
                result = ((BigDecimal)op1).multiply((BigDecimal)op2);
            } else {
                result = ((BigDecimal)op1).multiply(new BigDecimal(op2.toString()));
            }
        } else {
            if (op2 instanceof BigDecimal) {
                result = new BigDecimal(op1.toString()).multiply((BigDecimal)op2);
            } else {
                result = new BigDecimal(op1.toString()).multiply(new BigDecimal(op2.toString()));
            }
        }
        return basicNumberFormatTransfer(result);
    }

    public static Number dividePrecise(Number op1, Number op2) {
        BigDecimal result;
        if (op1 instanceof BigDecimal) {
            if (op2 instanceof BigDecimal) {
                result = ((BigDecimal)op1).divide((BigDecimal)op2, DIVIDE_PRECISION, RoundingMode.HALF_UP);
            } else {
                result = ((BigDecimal)op1).divide(new BigDecimal(op2.toString()), DIVIDE_PRECISION,
                    RoundingMode.HALF_UP);
            }
        } else {
            if (op2 instanceof BigDecimal) {
                result = new BigDecimal(op1.toString()).divide((BigDecimal)op2, DIVIDE_PRECISION,
                    RoundingMode.HALF_UP);
            } else {
                result = new BigDecimal(op1.toString()).divide(new BigDecimal(op2.toString()), DIVIDE_PRECISION,
                    RoundingMode.HALF_UP);
            }
        }
        return basicNumberFormatTransfer(result);
    }

    /**
     * 格式转化通用
     * @param number
     * @return
     */
    protected static Number basicNumberFormatTransfer(BigDecimal number){
        if (number.scale() == 0) {
            if(number.compareTo(OperatorOfNumber.BIG_DECIMAL_INTEGER_MAX) < OperatorOfNumber.MORE
                        && number.compareTo(OperatorOfNumber.BIG_DECIMAL_INTEGER_MIN) > OperatorOfNumber.LESS){
                return number.intValue();
            }else if(number.compareTo(OperatorOfNumber.BIG_DECIMAL_LONG_MAX) < OperatorOfNumber.MORE
                            && number.compareTo(OperatorOfNumber.BIG_DECIMAL_LONG_MIN) > OperatorOfNumber.LESS){
                return number.longValue();
            }
        }
        return number;
    }
}
