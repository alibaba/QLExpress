package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: new scope
 * Input: 0
 * Output: 0
 *
 * Author: DQinYuan
 */
public class NewScopeInstruction extends QLInstruction {
    
    private final String scopeName;
    
    public NewScopeInstruction(ErrorReporter errorReporter, String scopeName) {
        super(errorReporter);
        this.scopeName = scopeName;
    }
    
    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        qContext.newScope();
        return QResult.NEXT_INSTRUCTION;
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
        PrintlnUtils.printlnByCurDepth(depth, index + ": NewScope " + scopeName, debug);
    }
}
