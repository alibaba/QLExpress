package com.alibaba.qlexpress4.aparser.compiletimefunction;

import com.alibaba.qlexpress4.aparser.OperatorFactory;
import com.alibaba.qlexpress4.aparser.QLGrammarParser;
import com.alibaba.qlexpress4.aparser.compiletimefunction.CodeGenerator;

import java.util.List;

/**
 * Author: DQinYuan
 */
public interface CompileTimeFunction {

    /**
     * create instructions for function in compile time
     * @param functionName function name
     * @param arguments arguments syntax tree
     * @param operatorFactory operator factory
     * @param codeGenerator tool for code generate
     */
    void createFunctionInstruction(String functionName, List<QLGrammarParser.ExpressionContext> arguments,
                                   OperatorFactory operatorFactory, CodeGenerator codeGenerator);

}
