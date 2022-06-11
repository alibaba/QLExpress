package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;

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
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        qRuntime.push(getOrCreateSymbol(qRuntime));
    }

    private LeftValue getOrCreateSymbol(QRuntime qRuntime) {
        LeftValue symbol = qRuntime.getSymbol(name);
        if (symbol == null) {
            symbol = new AssignableDataValue(null);
            qRuntime.defineSymbol(name, Object.class, symbol);
        }
        return symbol;
    }
}
