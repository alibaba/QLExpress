package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.exception.QLException;

import static com.ql.util.express.config.QLExpressRunStrategy.isCompareNullLessMoreAsFalse;

/**
 * 处理比较操作符号
 */
public class OperatorEqualsLessMore extends Operator {
    public OperatorEqualsLessMore(String name) {
        this.name = name;
    }

    public OperatorEqualsLessMore(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) throws Exception {
        return executeInner(this.name, op1, op2);
    }

    public static boolean executeInner(String opStr, Object obj1, Object obj2) throws Exception {
        if ("==".equals(opStr)) {
            return Operator.objectEquals(obj1, obj2);
        }
        if ("!=".equals(opStr) || "<>".equals(opStr)) {
            return !Operator.objectEquals(obj1, obj2);
        }

        // 进行其他大小比较操作
        if (obj1 == null || obj2 == null) {
            if (isCompareNullLessMoreAsFalse()) {
                return false;
            }
            throw new QLException("空操作数无法进行数字比较操作：left = " + obj1 + ",right = " + obj2);
        }
        int i = Operator.compareData(obj1, obj2);
        boolean result = false;
        if (i > 0) {
            result = ">".equals(opStr) || ">=".equals(opStr) || "!=".equals(opStr) || "<>".equals(opStr);
        } else if (i == 0) {
            result = "=".equals(opStr) || "==".equals(opStr) || ">=".equals(opStr) || "<=".equals(opStr);
        } else if (i < 0) {
            result = "<".equals(opStr) || "<=".equals(opStr) || "!=".equals(opStr) || "<>".equals(opStr);
        }
        return result;
    }
}
