package com.alibaba.qlexpress4.parser.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * AST node for interpolated strings (e.g., "hello, ${name}").
 *
 * <p>An interpolated string consists of multiple segments:
 * <ul>
 *   <li>Static text segments (e.g., "hello, ")</li>
 *   <li>Expression segments (e.g., ${name})</li>
 * </ul>
 *
 * <p>Each segment can be either:
 * <ul>
 *   <li>A String representing static text</li>
 *   <li>An ExpressionNode representing an interpolated expression</li>
 * </ul>
 */
public class InterpolatedStringNode extends ASTNode implements ExpressionNode {
    private final List<Object> segments;
    
    public InterpolatedStringNode(int line, int column, int startPosition, String source) {
        super(line, column, startPosition, source);
        this.segments = new ArrayList<>();
    }
    
    public InterpolatedStringNode(int line, int column, int startPosition, String source, List<Object> segments) {
        super(line, column, startPosition, source);
        this.segments = segments != null ? segments : new ArrayList<>();
    }
    
    @Override
    public <R, C> R accept(ASTVisitor<R, C> visitor, C context)
        throws Exception {
        return visitor.visit(this, context);
    }
    
    /**
     * Adds a segment to this interpolated string.
     *
     * @param segment either a String (static text) or an ExpressionNode (interpolated expression)
     */
    public void addSegment(Object segment) {
        segments.add(segment);
    }
    
    /**
     * Returns the list of segments in this interpolated string.
     *
     * @return the list of segments (each is either a String or an ExpressionNode)
     */
    public List<Object> getSegments() {
        return segments;
    }
    
    /**
     * Returns the number of segments in this interpolated string.
     *
     * @return the number of segments
     */
    public int getSegmentCount() {
        return segments.size();
    }
}
