package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: pop the top of stack, if the element is ${expect}, jump to position, else execute next instruction as normal. jump if null
 * @Input: 1
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class JumpIfPopInstruction extends QLInstruction {

    private final boolean expect;

    private int position;

    public JumpIfPopInstruction(ErrorReporter errorReporter, boolean expect, int position) {
        super(errorReporter);
        this.expect = expect;
        this.position = position;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        boolean conditionBool = conditionToBool(qContext.pop().get());
        return conditionBool == expect? new QResult(new DataValue(position), QResult.ResultType.JUMP):
                QResult.NEXT_INSTRUCTION;
    }

    private boolean conditionToBool(Object condition) {
        if (condition == null) {
            return expect;
        }
        if (!(condition instanceof Boolean)) {
            throw errorReporter.report("CONDITION_EXPECT_BOOL",
                    "condition expression result must be bool");
        }
        return (boolean) condition;
    }

    @Override
    public int stackInput() {
        return 1;
    }

    @Override
    public int stackOutput() {
        return 0;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": JumpIfPop " + expect + " " + position, debug);
    }
}
