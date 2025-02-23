package com.alibaba.qlexpress4.runtime.trace;

public enum TraceType {
    // parent
    OPERATOR,
    FUNCTION,
    METHOD,
    FIELD,
    // children
    VARIABLE,
    VALUE,
    // other composite children
    PRIMARY
}
