package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * @Operation: if top of stack is true, execute ${thenBody}, else execute ${elseBody}
 * @Input: 1
 * @Output: 0
 * <p>
 * Author: DQinYuan
 */
public class IfInstruction extends QLInstruction {

    private final QLambda thenBody;

    private final QLambda elseBody;

    public IfInstruction(ErrorReporter errorReporter, QLambda thenBody, QLambda elseBody) {
        super(errorReporter);
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {

    }
}
