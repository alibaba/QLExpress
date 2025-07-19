package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;

/**
 * Author: DQinYuan
 */
public interface OperatorFactory {
    
    BinaryOperator getBinaryOperator(String operatorLexeme);
    
    UnaryOperator getPrefixUnaryOperator(String operatorLexeme);
    
    UnaryOperator getSuffixUnaryOperator(String operatorLexeme);
    
}
