package com.alibaba.qlexpress4.aparser.compiletimefunction;

import com.alibaba.qlexpress4.aparser.QLParser;
import com.alibaba.qlexpress4.aparser.ParseTree;
import com.alibaba.qlexpress4.aparser.Token;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.runtime.QLambdaDefinition;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.List;

/**
 * Author: DQinYuan
 */
public interface CodeGenerator {
    
    void addInstruction(QLInstruction qlInstruction);
    
    void addInstructionsByTree(ParseTree tree);
    
    QLSyntaxException reportParseErr(String errCode, String errReason);
    
    QLambdaDefinition generateLambdaDefinition(QLParser.ExpressionContext expressionContext,
        List<QLambdaDefinitionInner.Param> params);
    
    ErrorReporter getErrorReporter();
    
    ErrorReporter newReporterWithToken(Token token);
}
