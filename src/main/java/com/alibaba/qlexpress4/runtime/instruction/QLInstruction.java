package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;

import java.util.function.Consumer;

/**
 * Instruction Specification:
 *
 * @Operation: What does it do?
 * @Input: How many stack element it consumes? and their means
 * @Output: How many stack element it push back? and their means
 * <p>
 * Author: DQinYuan
 */
public abstract class QLInstruction {

    protected final ErrorReporter errorReporter;

    public QLInstruction(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    public abstract QResult execute(int index, QContext qContext, QLOptions qlOptions);

    /**
     * @return input size
     */
    public abstract int stackInput();

    /**
     * @return output size
     */
    public abstract int stackOutput();

    public abstract void println(int index, int depth, Consumer<String> debug);

    public ErrorReporter getErrorReporter() {
        return errorReporter;
    }
}
