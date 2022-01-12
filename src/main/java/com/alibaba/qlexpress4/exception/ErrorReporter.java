package com.alibaba.qlexpress4.exception;

public interface ErrorReporter {

    QLRuntimeException report(String reason);

}
