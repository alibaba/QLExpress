package com.alibaba.qlexpress4.aparser.compiletimefunction;

import com.alibaba.qlexpress4.common.OperatorFactory;

/**
 * Compile-time function interface.
 *
 * <p>This interface allows defining custom functions that are processed at compile time
 * rather than runtime. The original ANTLR-based implementation used
 * QLParser.ExpressionContext for AST nodes.</p>
 *
 * <p>As of 2026, QLExpress uses a hand-written recursive descent parser. This interface
 * is preserved for backward compatibility but requires reimplementation to work with the
 * new parser's AST structure (com.alibaba.qlexpress4.parser.ast package).</p>
 *
 * <p>The compile-time function feature is currently disabled pending migration to the
 * new parser architecture.</p>
 *
 * Author: DQinYuan
 * @deprecated Compile-time functions are not yet supported with the new parser
 */
@Deprecated
public interface CompileTimeFunction {

    /**
     * Create instructions for function at compile time.
     *
     * @param functionName function name
     * @param arguments arguments syntax tree (currently not supported with new parser)
     * @param operatorFactory operator factory
     * @param codeGenerator tool for code generation (currently not supported)
     * @throws UnsupportedOperationException always - feature not yet migrated
     */
    void createFunctionInstruction(String functionName, Object arguments,
        OperatorFactory operatorFactory, Object codeGenerator);

}
