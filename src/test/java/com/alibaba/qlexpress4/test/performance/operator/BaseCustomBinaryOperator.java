package com.alibaba.qlexpress4.test.performance.operator;

import java.util.Collection;
import java.util.Objects;

import com.alibaba.qlexpress4.runtime.operator.CustomBinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * @author 冰够
 */
public abstract class BaseCustomBinaryOperator implements CustomBinaryOperator {
    protected boolean equals(Object left, Object right) {
        if (Objects.equals(left, right)) {
            return true;
        }

        if (isBothNumber(left, right)) {
            return NumberMath.compareTo((Number)left, (Number)right) == 0;
        }

        if (isBothString(left, right)) {
            return left.toString().equals(right.toString());
        }

        return false;
    }

    protected boolean hasIntersect(Collection<?> leftCollection, Collection<?> rightCollection) {
        if (leftCollection == null || leftCollection.isEmpty() || rightCollection == null || rightCollection.isEmpty()) {
            return false;
        }

        for (Object rightItem : rightCollection) {
            for (Object leftItem : leftCollection) {
                if (equals(leftItem, rightItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBothNumber(Object left, Object right) {
        return left instanceof Number && right instanceof Number;
    }

    private boolean isBothString(Object left, Object right) {
        return left instanceof String && right instanceof String;
    }
}
