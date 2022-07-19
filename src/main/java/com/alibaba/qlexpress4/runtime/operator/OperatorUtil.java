//package com.alibaba.qlexpress4.runtime.operator;
//
//import com.alibaba.qlexpress4.exception.ErrorReporter;
//import com.alibaba.qlexpress4.runtime.Value;
//import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;
//import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;
//
///**
// * @author 冰够
// */
//public class OperatorUtil {
//    public Object plus(Value left, Value right, ErrorReporter errorReporter) {
//        Object leftValue = left.get();
//        Object rightValue = right.get();
//
//        if (leftValue instanceof String) {
//            return (String)leftValue + rightValue;
//        }
//
//        if (rightValue instanceof String) {
//            return leftValue + (String)rightValue;
//        }
//
//        if (isBothNumbers(left, right)) {
//            return NumberMath.add((Number)leftValue, (Number)rightValue);
//        }
//
//        throw buildInvalidOperandTypeException(left, right, errorReporter);
//    }
//}
