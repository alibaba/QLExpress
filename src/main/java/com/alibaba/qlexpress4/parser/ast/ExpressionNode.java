package com.alibaba.qlexpress4.parser.ast;

/**
 * Base interface for all expression nodes.
 *
 * <p>Expressions are nodes that produce a value when evaluated.
 * Examples include: literals, identifiers, binary operations, function calls, etc.
 */
public interface ExpressionNode extends Node {
    // Marker interface for expression nodes
}
