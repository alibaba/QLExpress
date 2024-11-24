package com.alibaba.qlexpress4.aparser;

/**
 * How to manage string interpolation, for instance, "a ${t-c} b"
 */
public enum InterpolationMode {
    /**
     * Implement interpolation using a QLExpress script.
     */
    SCRIPT,
    /**
     * Implement interpolation using a variable name in context.
     */
    VARIABLE
}
