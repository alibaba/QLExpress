package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.CheckOptions;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.operator.OperatorCheckStrategy;
import org.antlr.v4.runtime.Token;

/**
 * @author zhoutao
 */
public class CheckVisitor extends QLParserBaseVisitor<Void> {
    
    /**
     * Operator restriction strategy
     */
    private final OperatorCheckStrategy operatorCheckStrategy;
    
    /**
     * Script content for error reporting
     */
    private final String script;
    
    public CheckVisitor(CheckOptions checkOptions) {
        this(checkOptions, "");
    }
    
    public CheckVisitor(CheckOptions checkOptions, String script) {
        if (checkOptions == null) {
            checkOptions = CheckOptions.DEFAULT_OPTIONS;
        }
        
        this.operatorCheckStrategy = checkOptions.getCheckStrategy();
        this.script = script;
    }
    
    private void checkOperator(String operatorString, Token token)
        throws QLSyntaxException {
        if (null != operatorCheckStrategy && !operatorCheckStrategy.isAllowed(operatorString)) {
            String reason = String.format(QLErrorCodes.OPERATOR_NOT_ALLOWED.getErrorMsg(),
                operatorString,
                operatorCheckStrategy.getOperators());
            throw QLException.reportScannerErr(script,
                token.getStartIndex(),
                token.getLine(),
                token.getCharPositionInLine(),
                operatorString,
                QLErrorCodes.OPERATOR_NOT_ALLOWED.name(),
                reason);
        }
    }
    
    @Override
    public Void visitLeftAsso(QLParser.LeftAssoContext ctx) {
        // Get operator
        QLParser.BinaryopContext binaryopContext = ctx.binaryop();
        if (binaryopContext != null) {
            String operator = binaryopContext.getText();
            checkOperator(operator, binaryopContext.getStart()); // Validate here, may throw exception
        }
        
        // Continue traversing child nodes
        return super.visitLeftAsso(ctx);
    }
    
    @Override
    public Void visitPrefixExpress(QLParser.PrefixExpressContext ctx) {
        // Get prefix operator
        if (ctx.opId() != null) {
            String operator = ctx.opId().getText();
            checkOperator(operator, ctx.opId().getStart()); // Validate here, may throw exception
        }
        
        return super.visitPrefixExpress(ctx);
    }
    
    @Override
    public Void visitSuffixExpress(QLParser.SuffixExpressContext ctx) {
        // Get suffix operator
        if (ctx.opId() != null) {
            String operator = ctx.opId().getText();
            checkOperator(operator, ctx.opId().getStart());
        }
        
        return super.visitSuffixExpress(ctx);
    }
    
    @Override
    public Void visitExpression(QLParser.ExpressionContext ctx) {
        // Check assignment operator
        if (ctx.assignOperator() != null) {
            checkOperator(ctx.assignOperator().getText(), ctx.assignOperator().getStart());
        }
        
        return super.visitExpression(ctx);
    }
}
