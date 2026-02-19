package com.alibaba.qlexpress4.parser.ast;

/**
 * Base interface for all statement nodes.
 *
 * <p>Statements are nodes that perform actions but do not necessarily produce a value.
 * Examples include: if-else, while, for, return, variable declarations, etc.
 */
public interface StatementNode extends Node {
    // Marker interface for statement nodes
}
