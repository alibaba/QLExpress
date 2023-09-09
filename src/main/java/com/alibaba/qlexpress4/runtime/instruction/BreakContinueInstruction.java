package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: return break object and exit lambda
 * @Input: 0
 * @Output: 0
 * <p>
 * Author: DQinYuan
 */
public class BreakContinueInstruction extends QLInstruction {

    private final QResult result;

    public BreakContinueInstruction(ErrorReporter errorReporter, QResult result) {
        super(errorReporter);
        this.result = result;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        return result;
    }

    @Override
    public int stackInput() {
        return 0;
    }

    @Override
    public int stackOutput() {
        return 0;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        String breakContinue = result == QResult.LOOP_BREAK_RESULT? "Break": "Continue";
        PrintlnUtils.printlnByCurDepth(depth, index + ": " + breakContinue, debug);
    }
}
