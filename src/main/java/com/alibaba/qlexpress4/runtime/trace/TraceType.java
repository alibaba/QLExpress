package com.alibaba.qlexpress4.runtime.trace;

public enum TraceType {
    // parent
    OPERATOR, FUNCTION, METHOD, FIELD, LIST, MAP, IF, RETURN, BLOCK,
    // children
    VARIABLE, VALUE, DEFINE_FUNCTION, DEFINE_MACRO,
    // other composite children
    PRIMARY, STATEMENT
}
