package com.alibaba.qlexpress4.parser.visitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Context for instruction generation.
 * <p>
 * Tracks state during AST traversal, including variable scopes,
 * loop labels, and jump targets.
 *
 * @author QLExpress Team
 */
public class GenerationContext {
    
    private final GenerationContext parent;
    
    private final Map<String, Object> properties;
    
    public GenerationContext() {
        this(null);
    }
    
    public GenerationContext(GenerationContext parent) {
        this.parent = parent;
        this.properties = new HashMap<>();
    }
    
    /**
     * Creates a child context for nested scopes (blocks, loops, etc.).
     */
    public GenerationContext createChildContext() {
        return new GenerationContext(this);
    }
    
    /**
     * Gets a property value from this context or any parent context.
     */
    public Object getProperty(String key) {
        Object value = properties.get(key);
        if (value == null && parent != null) {
            return parent.getProperty(key);
        }
        return value;
    }
    
    /**
     * Sets a property value in this context.
     */
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    /**
     * Returns the parent context, or null if this is the root context.
     */
    public GenerationContext getParent() {
        return parent;
    }
}
