package com.alibaba.qlexpress4.runtime.operator;

import java.util.Objects;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * TODO bingo
 * 1. 是否支持Character类型的比较 com.ql.util.express.Operator#objectEquals(java.lang.Object, java.lang.Object)
 * 2. 是否支持实现Comparable接口的对象比较 groovy支持
 * 3. boolean, Date, String 类型比较
 * 4. null 如何比较 org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation#compareToWithEqualityCheck
 *
 * @author 冰够
 */
public class CompareOperator extends BaseOperator {
    public CompareOperator(String operator) {
        super(operator);
    }

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        int compareResult = compare(left, right, errorReporter);
        switch (operator) {
            case "==":
                return compareResult == 0;
            case "!=":
                return compareResult == -1 || compareResult == 1;
            case "<":
                return compareResult == -1;
            case "<=":
                return compareResult == -1 || compareResult == 0;
            case ">":
                return compareResult == 1;
            case ">=":
                return compareResult == 0 || compareResult == 1;
            default:
                throw buildInvalidOperandTypeException(left, right, errorReporter);
        }
    }

    @Override
    public int getPrecedence() {
        return 0;
    }

    private int compare(Value left, Value right, ErrorReporter errorReporter) {
        if (Objects.equals(left.get(), right.get())) {
            return 0;
        }

        if (isSameType(left, right) && instanceofComparable(left)) {
            return ((Comparable)left).compareTo(right);
        }

        if (isBothNumbers(left, right)) {
            return NumberMath.compareTo((Number)left.get(), (Number)right.get());
        }

        throw buildInvalidOperandTypeException(left, right, errorReporter);
    }
}
