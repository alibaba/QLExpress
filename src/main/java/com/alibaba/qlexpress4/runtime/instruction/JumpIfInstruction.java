package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: if the element is ${expect}, jump to position, else execute next instruction as normal. not jump if null
 * @Input: 0
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class JumpIfInstruction extends QLInstruction {

    public static final JumpIfInstruction INSTANCE = new JumpIfInstruction(null,
            false, -1);

    private final boolean expect;

    private final int position;

    public JumpIfInstruction(ErrorReporter errorReporter, boolean expect, int position) {
        super(errorReporter);
        this.expect = expect;
        this.position = position;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        boolean conditionBool = conditionToBool(qContext.peek().get());
        return conditionBool == expect? new QResult(new DataValue(position), QResult.ResultType.JUMP):
                QResult.NEXT_INSTRUCTION;
    }

    private boolean conditionToBool(Object condition) {
        if (condition == null) {
            return !expect;
        }
        if (!(condition instanceof Boolean)) {
            throw errorReporter.report("CONDITION_EXPECT_BOOL",
                    "condition expression result must be bool");
        }
        return (boolean) condition;
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
        PrintlnUtils.printlnByCurDepth(depth, "JumpIf " + expect + " " + position, debug);
    }
}
