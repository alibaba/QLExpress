package com.ql.util.express.instruction.op;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.ql.util.express.Operator;

public class OperatorUnaryMinus extends Operator {
    public OperatorUnaryMinus(String name) {
        this.name = name;
    }

    public OperatorUnaryMinus(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list[0] instanceof Number) {
            return negate((Number) list[0]);
        }
        throw new Exception("Cannot apply unary minus to non-number type");
    }

    public static Number negate(Number number) {
        if (number == null) {
            return null;
        }

        if (number instanceof Integer) {
            return -number.intValue();
        } else if (number instanceof Long) {
            return -number.longValue();
        } else if (number instanceof Float) {
            return -number.floatValue();
        } else if (number instanceof Double) {
            return -number.doubleValue();
        } else if (number instanceof BigInteger) {
            return ((BigInteger) number).negate();
        } else if (number instanceof BigDecimal) {
            return ((BigDecimal) number).negate();
        } else if (number instanceof Short) {
            return (short) -number.shortValue();
        } else if (number instanceof Byte) {
            return (byte) -number.byteValue();
        } else {
            // 对于其他类型的 Number
            return -number.doubleValue();
        }
    }
}
