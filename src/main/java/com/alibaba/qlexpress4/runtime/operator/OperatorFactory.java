package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;

/**
 * Author: DQinYuan
 */
public class OperatorFactory {

    public static BinaryOperator getOperator(String operatorLexeme) {
        return null;
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
