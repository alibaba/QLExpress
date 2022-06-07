package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Operation: if top of stack is true, execute ${thenBody}, else execute ${elseBody}
 * @Input: 1
 * @Output: 0
 *
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
        Object condition = qRuntime.pop().get();
        if (!(condition instanceof Boolean)) {
            throw errorReporter.report("IF_CONDITION_NOT_BOOL",
                    "if condition expression result must be bool");
        }
        boolean conditionBool = (boolean) condition;
        try {
            if (conditionBool) {
                thenBody.call();
            } else if (elseBody != null) {
                elseBody.call();
            }
        } catch (Exception e) {
            if (e instanceof QLRuntimeException) {
                throw (QLRuntimeException) e;
            }
            // should not run there
            throw errorReporter.report("IF_UNKNOWN_EXCEPTION", "if unknown exception");
        }
    }
}
