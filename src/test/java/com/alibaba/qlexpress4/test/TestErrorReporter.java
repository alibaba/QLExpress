package com.alibaba.qlexpress4.test;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLRuntimeException;

/**
 * @Author TaoKan
 * @Date 2022/6/22 下午7:02
 */
public class TestErrorReporter implements ErrorReporter {
    @Override
    public QLRuntimeException report(Object attachment, String errorCode, String reason) {
        return null;
    }

    @Override
    public QLRuntimeException report(String errorCode, String reason) {
        return null;
    }

    @Override
    public QLRuntimeException reportFormat(String errorCode, String format, Object... args) {
        return null;
    }
}
