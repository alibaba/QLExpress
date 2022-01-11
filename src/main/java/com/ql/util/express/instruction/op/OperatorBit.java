package com.ql.util.express.instruction.op;

import java.util.Arrays;

import com.ql.util.express.Operator;
import com.ql.util.express.exception.QLException;

/**
 * Created by tianqiao on 16/12/15.
 */
public class OperatorBit extends Operator {
    public OperatorBit(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if ("~".equals(this.name)) {
            if (list.length == 1 && list[0] instanceof Number) {
                if (list[0] instanceof Integer) {
                    return ~(((Number)list[0]).intValue());
                } else {
                    return ~(((Number)list[0]).longValue());
                }
            } else {
                throw new QLException("取反操作符 ~ 参数不合法:" + Arrays.toString(list));
            }
        }
        if ("&".equals(this.name)) {
            if (list.length == 2 && list[0] instanceof Number && list[1] instanceof Number) {
                if (list[0] instanceof Integer && list[1] instanceof Integer) {
                    return (Integer)list[0] & (Integer)list[1];
                }
                return (((Number)list[0]).longValue()) & (((Number)list[1]).longValue());
            } else {
                throw new QLException("按位与操作符 & 两边的参数不合法:" + Arrays.toString(list));
            }
        }
        if ("|".equals(this.name)) {
            if (list.length == 2 && list[0] instanceof Number && list[1] instanceof Number) {
                if (list[0] instanceof Integer && list[1] instanceof Integer) {
                    return (Integer)list[0] | (Integer)list[1];
                }
                return (((Number)list[0]).longValue()) | (((Number)list[1]).longValue());
            } else {
                throw new QLException("按位或操作符 | 两边的参数不合法:" + Arrays.toString(list));
            }
        }
        if ("^".equals(this.name)) {
            if (list.length == 2 && list[0] instanceof Number && list[1] instanceof Number) {
                if (list[0] instanceof Integer && list[1] instanceof Integer) {
                    return (Integer)list[0] ^ (Integer)list[1];
                }
                return (((Number)list[0]).longValue()) ^ (((Number)list[1]).longValue());
            } else {
                throw new QLException("按位异或操作符 ^ 两边的参数不合法:" + Arrays.toString(list));
            }
        }
        if ("<<".equals(this.name)) {
            if (list.length == 2 && list[0] instanceof Number && list[1] instanceof Number) {
                if (list[0] instanceof Integer && list[1] instanceof Integer) {
                    return (Integer)list[0] << (Integer)list[1];
                }
                return (((Number)list[0]).longValue()) << (((Number)list[1]).longValue());
            } else {
                throw new QLException("左移操作符 << 两边的参数不合法:" + Arrays.toString(list));
            }
        }
        if (">>".equals(this.name)) {
            if (list.length == 2 && list[0] instanceof Number && list[1] instanceof Number) {
                if (list[0] instanceof Integer && list[1] instanceof Integer) {
                    return (Integer)list[0] >> (Integer)list[1];
                }
                return (((Number)list[0]).longValue()) >> (((Number)list[1]).longValue());
            } else {
                throw new QLException("右移操作符 >> 两边的参数不合法:" + Arrays.toString(list));
            }
        }
        throw new QLException("不支持的位运算操作符:" + Arrays.toString(list));
    }
}
