package com.alibaba.qlexpress4.runtime.trace;

public enum TraceType {
    // parent
    OPERATOR,
    FUNCTION,
    METHOD,
    FIELD,
    LIST,
    MAP,
    IF,
    RETURN,
    // children
    VARIABLE,
    VALUE,
    // other composite children
    PRIMARY
}
