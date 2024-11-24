package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: concat n string on the top of stack
 * Input: ${n}
 * Output: concat result
 *
 * Author: DQinYuan
 */
public class StringJoinInstruction extends QLInstruction {

    private final int n;

    public StringJoinInstruction(ErrorReporter errorReporter, int n) {
        super(errorReporter);
        this.n = n;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Parameters arguments = qContext.pop(n);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(arguments.get(i).get());
        }
        qContext.push(new DataValue(sb.toString()));
        return QResult.NEXT_INSTRUCTION;
    }

    public int getN() {
        return n;
    }

    @Override
    public int stackInput() {
        return n;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": StringJoin " + n, debug);
    }
}
