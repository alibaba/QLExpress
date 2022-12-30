package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: if the element is ${expect}, jump to position, else execute next instruction as normal
 * @Input: 0
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class JumpIfInstruction extends QLInstruction {

    private final boolean expect;

    private final int gap;

    public JumpIfInstruction(ErrorReporter errorReporter, boolean expect, int gap) {
        super(errorReporter);
        this.expect = expect;
        this.gap = gap;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Object condition = qContext.peek().get();
        if (!(condition instanceof Boolean)) {
            throw errorReporter.report("CONDITION_EXPECT_BOOL",
                    "condition expression result must be bool");
        }
        boolean conditionBool = (boolean) condition;
        return conditionBool == expect? new QResult(new DataValue(gap), QResult.ResultType.JUMP):
                QResult.NEXT_INSTRUCTION;
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
        PrintlnUtils.printlnByCurDepth(depth, "JumpIf " + expect + " " + gap, debug);
    }
}
