package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.runtime.operator.arithmetic.DivideOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MinusOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.ModOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MultiplyOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.PlusOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseAndOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseLeftShiftOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseRightShiftOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseRightShiftUnsignedOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseXorOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.EqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.GreaterEqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.GreaterOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.LessEqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.LessOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.UnequalOperator;
import com.alibaba.qlexpress4.runtime.operator.logic.LogicAndOperator;
import com.alibaba.qlexpress4.runtime.operator.logic.LogicOrOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;

/**
 * Author: DQinYuan
 */
public class OperatorFactory {
    /**
     * 获取二院操作符
     *
     * @param operatorLexeme +, =, *, /
     * @return
     */
    public static BinaryOperator getBinaryOperator(String operatorLexeme) {
        switch (operatorLexeme) {
            case "+":
                return new PlusOperator();
            case "-":
                return new MinusOperator();
            case "*":
                return new MultiplyOperator();
            case "/":
                return new DivideOperator();
            case "%":
            case "mod": // TODO bingo 是否需要
                return new ModOperator();
            case "&":
                return new BitwiseAndOperator();
            case "|":
                return new BitwiseAndOperator();
            case "^":
                return new BitwiseXorOperator();
            case "<<":
                return new BitwiseLeftShiftOperator();
            case ">>":
                return new BitwiseRightShiftOperator();
            case ">>>":
                return new BitwiseRightShiftUnsignedOperator();
            case "&&":
            case "and":// TODO bingo 是否需要
                return new LogicAndOperator();
            case "||":
            case "or":// TODO bingo 是否需要
                return new LogicOrOperator();
            case "==":
                return new EqualOperator();
            case "!=":
                return new UnequalOperator();
            case ">":
                return new GreaterOperator();
            case ">=":
                return new GreaterEqualOperator();
            case "<":
                return new LessOperator();
            case "<=":
                return new LessEqualOperator();
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
        return null;
    }

    /**
     * like 1-- 1++
     *
     * @param operatorLexeme
     * @return
     */
    public static UnaryOperator getSuffixUnaryOperator(String operatorLexeme) {
        return null;
    }
}
