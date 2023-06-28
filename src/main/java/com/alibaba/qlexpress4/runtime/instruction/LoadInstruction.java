package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: load variable from local to global scope, create when not exist
 * @Input: 0
 * @Output: 1 left value of local variable
 *
 * Author: DQinYuan
 */
public class LoadInstruction extends QLInstruction {

    private final String name;

    public LoadInstruction(ErrorReporter errorReporter, String name) {
        super(errorReporter);
        this.name = name;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        qContext.push(qContext.getSymbol(name));
        return QResult.NEXT_INSTRUCTION;
    }

    @Override
    public int stackInput() {
        return 0;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    public String getName() {
        return name;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "Load " + name, debug);
    }
}
