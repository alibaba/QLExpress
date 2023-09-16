package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Operation: new a List with top ${initLength} stack element
 * Input: ${initLength}
 * Output: 1
 * <p>
 * Author: DQinYuan
 */
public class NewListInstruction extends QLInstruction {

    private final int initLength;

    public NewListInstruction(ErrorReporter errorReporter, int initLength) {
        super(errorReporter);
        this.initLength = initLength;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Parameters initItems = qContext.pop(initLength);
        List<? super Object> l = new ArrayList<>(initLength);
        // TODO: 遍历逻辑优化
        for (int i = 0; i < initLength; i++) {
            l.add(initItems.getValue(i));
        }
        qContext.push(new DataValue(l));
        return QResult.NEXT_INSTRUCTION;
    }

    @Override
    public int stackInput() {
        return initLength;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": NewList " + initLength, debug);
    }
}
