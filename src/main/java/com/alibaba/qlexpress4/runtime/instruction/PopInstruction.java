package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: pop top element
 * Input: 1
 * Output: 0
 * <p>
 * Author: DQinYuan
 */
public class PopInstruction extends QLInstruction {
    public PopInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }
    
    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        qContext.pop();
        return QResult.NEXT_INSTRUCTION;
    }
    
    @Override
    public int stackInput() {
        return 1;
    }
    
    @Override
    public int stackOutput() {
        return 0;
    }
    
    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": Pop", debug);
    }
}
