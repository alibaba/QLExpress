package com.alibaba.qlexpress4.parser.ast;

/**
 * Base interface for all expression nodes.
 *
 * <p>Expressions are nodes that produce a value when evaluated.
 * Examples include: literals, identifiers, binary operations, function calls, etc.
 *
 * <p>In QLExpress, any expression can also be used as a statement.
 */
public interface ExpressionNode extends Node, StatementNode {
    // Marker interface for expression nodes
}
