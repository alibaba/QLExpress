package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.runtime.operator.arithmetic.DivideOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MinusOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.ModOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MultiplyOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.PlusOperator;
import com.alibaba.qlexpress4.runtime.operator.assign.AssignOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseAndOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseInvertOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseLeftShiftOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseOrOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseRightShiftOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseRightShiftUnsignedOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseXorOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.EqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.GreaterEqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.GreaterOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.LessEqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.LessOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.UnequalOperator;
import com.alibaba.qlexpress4.runtime.operator.in.InOperator;
import com.alibaba.qlexpress4.runtime.operator.logic.LogicAndOperator;
import com.alibaba.qlexpress4.runtime.operator.logic.LogicNotOperator;
import com.alibaba.qlexpress4.runtime.operator.logic.LogicOrOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.MinusMinusPrefixUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.MinusMinusSuffixUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.MinusUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.PlusPlusPrefixUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.PlusPlusSuffixUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.PlusUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;

/**
 * @author 冰够
 */
public class OperatorFactory {
    /**
     * 获取二元操作符
     * TODO: 这个不能是静态方法，需要考虑用户自定义操作符
     * TODO: 缺少赋值操作符
     *
     * @param operatorLexeme +, =, *, /
     * @return
     */
    public static BinaryOperator getBinaryOperator(String operatorLexeme) {
        switch (operatorLexeme) {
            case "=":
                return AssignOperator.getInstance();
            case "+":
                return PlusOperator.getInstance();
            case "-":
                return MinusOperator.getInstance();
            case "*":
                return MultiplyOperator.getInstance();
            case "/":
                return DivideOperator.getInstance();
            case "%":
                return ModOperator.getInstance("%");
            case "mod":
                return ModOperator.getInstance("mod");
            case "&":
                return BitwiseAndOperator.getInstance();
            case "|":
                return BitwiseOrOperator.getInstance();
            case "^":
                return BitwiseXorOperator.getInstance();
            case "<<":
                return BitwiseLeftShiftOperator.getInstance();
            case ">>":
                return BitwiseRightShiftOperator.getInstance();
            case ">>>":
                return BitwiseRightShiftUnsignedOperator.getInstance();
            case "&&":
                return LogicAndOperator.getInstance("&&");
            case "and":
                return LogicAndOperator.getInstance("and");
            case "||":
                return LogicOrOperator.getInstance("||");
            case "or":
                return LogicOrOperator.getInstance("or");
            case "==":
                return EqualOperator.getInstance();
            case "!=":
                return UnequalOperator.getInstance();
            case ">":
                return GreaterOperator.getInstance();
            case ">=":
                return GreaterEqualOperator.getInstance();
            case "<":
                return LessOperator.getInstance();
            case "<=":
                return LessEqualOperator.getInstance();
            case "in":
                return InOperator.getInstance();
            default:
                return null;
        }
    }

    /**
     * like --1 ++1 !true ~1 ^1
     *
     * @param operatorLexeme ++, --
     * @return
     */
    public static UnaryOperator getPrefixUnaryOperator(String operatorLexeme) {
        switch (operatorLexeme) {
            case "+":
                return PlusUnaryOperator.getInstance();
            case "-":
                return MinusUnaryOperator.getInstance();
            case "++":
                return PlusPlusPrefixUnaryOperator.getInstance();
            case "--":
                return MinusMinusPrefixUnaryOperator.getInstance();
            case "~":
                return BitwiseInvertOperator.getInstance();
            case "!":
                return LogicNotOperator.getInstance();
            default:
                return null;
        }
    }

    /**
     * like 1-- 1++
     *
     * @param operatorLexeme ++, --
     * @return
     */
    public static UnaryOperator getSuffixUnaryOperator(String operatorLexeme) {
        switch (operatorLexeme) {
            case "++":
                return PlusPlusSuffixUnaryOperator.getInstance();
            case "--":
                return MinusMinusSuffixUnaryOperator.getInstance();
            default:
                return null;
        }
    }
}
