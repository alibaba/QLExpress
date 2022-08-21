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
        qContext.push(getOrCreateSymbol(qContext));
        return QResult.CONTINUE_RESULT;
    }

    @Override
    public int stackInput() {
        return 0;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "Load " + name, debug);
    }

    private Value getOrCreateSymbol(QContext qContext) {
        Value symbol = qContext.getSymbol(name);
        if (symbol == null) {
            symbol = qContext.defineSymbol(name, Object.class);
        }
        return symbol;
    }
}
