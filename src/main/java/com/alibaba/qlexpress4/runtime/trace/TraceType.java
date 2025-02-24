package com.alibaba.qlexpress4.runtime.trace;

public enum TraceType {
    // parent
    OPERATOR,
    FUNCTION,
    METHOD,
    FIELD,
    LIST,
    // children
    VARIABLE,
    VALUE,
    // other composite children
    PRIMARY
}
