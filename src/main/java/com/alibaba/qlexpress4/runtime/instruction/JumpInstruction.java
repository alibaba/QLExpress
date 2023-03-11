package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: jump to a position
 * @Input: 0
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class JumpInstruction extends QLInstruction {

    public static final JumpInstruction INSTANCE = new JumpInstruction(null, -1);

    private final int position;

    public JumpInstruction(ErrorReporter errorReporter, int position) {
        super(errorReporter);
        this.position = position;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        return new QResult(new DataValue(position), QResult.ResultType.JUMP);
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
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "Jump " + position, debug);
    }
}
