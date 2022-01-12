package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @author 悬衡
 * date 2022/1/12 2:35 下午
 */
public abstract class QLInstruction {

    private final ErrorReporter errorReporter;

    private QLInstruction(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    public void execute() {
        throw errorReporter.report("type error");
    }
}
