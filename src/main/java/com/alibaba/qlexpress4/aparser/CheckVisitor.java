package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.CheckOptions;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.runtime.operator.Operator;
import org.antlr.v4.runtime.Token;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Operator validation visitor
 * Traverses syntax tree and performs operator restriction validation during traversal
 *
 * Core design: Validation logic is completely implemented within the Visitor, triggered through accept() method
 *
 * @author QLExpress Team
 */
public class CheckVisitor extends QLParserBaseVisitor<Void> {
    
    /**
     * Operator restriction strategy
     */
    private final CheckOptions.OperatorStrategy strategy;
    
    /**
     * Operator set (meaning depends on strategy)
     * - ALLOW_ALL: ignored
     * - WHITELIST: allowed operators
     * - BLACKLIST: forbidden operators
     */
    private final Set<String> operatorStrings;
    
    /**
     * Script content for error reporting
     */
    private final String script;
    
    /**
     * Constructor
     * @param checkOptions validation configuration, containing operator restriction strategy
     */
    public CheckVisitor(CheckOptions checkOptions) {
        this(checkOptions, "");
    }
    
    /**
     * Constructor with script for error reporting
     * @param checkOptions validation configuration, containing operator restriction strategy
     * @param script script content for error reporting
     */
    public CheckVisitor(CheckOptions checkOptions, String script) {
        if (checkOptions == null) {
            checkOptions = CheckOptions.DEFAULT_OPTIONS;
        }
        
        this.strategy = checkOptions.getStrategy();
        Set<Operator> operators = checkOptions.getOperators();
        
        // Convert Operator objects to string set for fast lookup
        this.operatorStrings = (operators != null && !operators.isEmpty())
            ? operators.stream().map(Operator::getOperator).collect(Collectors.toSet())
            : new HashSet<>();
        this.script = script;
    }
    
    /**
     * Check if operator is allowed, throw exception immediately if not allowed
     *
     * @param operatorString operator string
     * @param token token containing position information
     * @throws QLSyntaxException if operator is not in whitelist or is in blacklist
     */
    private void checkOperator(String operatorString, Token token)
        throws QLSyntaxException {
        switch (strategy) {
            case ALLOW_ALL:
                // No restriction
                return;
            
            case WHITELIST:
                if (operatorStrings == null || !operatorStrings.contains(operatorString)) {
                    String reason = String.format("Script uses disallowed operator: %s. Allowed operators: %s",
                        operatorString,
                        operatorStrings);
                    throw QLException.reportScannerErr(script,
                        token.getStartIndex(),
                        token.getLine(),
                        token.getCharPositionInLine(),
                        operatorString,
                        "OPERATOR_NOT_ALLOWED",
                        reason);
                }
                break;
            
            case BLACKLIST:
                if (operatorStrings != null && operatorStrings.contains(operatorString)) {
                    String reason = String.format("Script uses forbidden operator: %s. Forbidden operators: %s",
                        operatorString,
                        operatorStrings);
                    throw QLException.reportScannerErr(script,
                        token.getStartIndex(),
                        token.getLine(),
                        token.getCharPositionInLine(),
                        operatorString,
                        "OPERATOR_FORBIDDEN",
                        reason);
                }
                break;
        }
    }
    
    /**
     * Visit binary operator expressions (including arithmetic, logical, comparison operators, etc.)
     */
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
    
    /**
     * Visit prefix unary operator expressions (such as !, -, ++, --, etc.)
     */
    @Override
    public Void visitPrefixExpress(QLParser.PrefixExpressContext ctx) {
        // Get prefix operator
        if (ctx.opId() != null) {
            String operator = ctx.opId().getText();
            checkOperator(operator, ctx.opId().getStart()); // Validate here, may throw exception
        }
        
        return super.visitPrefixExpress(ctx);
    }
    
    /**
     * Visit suffix unary operator expressions (such as ++, --, etc.)
     */
    @Override
    public Void visitSuffixExpress(QLParser.SuffixExpressContext ctx) {
        // Get suffix operator
        if (ctx.opId() != null) {
            String operator = ctx.opId().getText();
            checkOperator(operator, ctx.opId().getStart());
        }
        
        return super.visitSuffixExpress(ctx);
    }
    
    /**
     * Visit expression (including assignment operators)
     */
    @Override
    public Void visitExpression(QLParser.ExpressionContext ctx) {
        // Check assignment operator
        if (ctx.assignOperator() != null) {
            checkOperator(ctx.assignOperator().getText(), ctx.assignOperator().getStart());
        }
        
        return super.visitExpression(ctx);
    }
}
