package com.alibaba.qlexpress4.aparser.compiletimefunction;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.runtime.QLambdaDefinition;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.List;

/**
 * Code generator interface for compile-time functions.
 *
 * <p>This interface was used by the ANTLR-based parser to generate code.
 * As of 2026, QLExpress uses a hand-written recursive descent parser.</p>
 *
 * <p>The compile-time function feature is currently disabled pending migration to the
 * new parser architecture.</p>
 *
 * @deprecated Compile-time functions are not yet supported with the new parser
 */
@Deprecated
public interface CodeGenerator {
    
    void addInstruction(QLInstruction qlInstruction);
    
    void addInstructionsByTree(Object tree);
    
    QLSyntaxException reportParseErr(String errCode, String errReason);
    
    QLambdaDefinition generateLambdaDefinition(Object expressionContext, List<QLambdaDefinitionInner.Param> params);
    
    ErrorReporter getErrorReporter();
    
    ErrorReporter newReporterWithToken(Object token);
}
