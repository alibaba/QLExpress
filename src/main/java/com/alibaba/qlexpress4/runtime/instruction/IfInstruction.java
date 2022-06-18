package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;

/**
 * @Operation: if top of stack is true, execute ${thenBody}, else execute ${elseBody}
 * @Input: 1
 * @Output: 1
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
        callBody(qRuntime, conditionBool? thenBody: elseBody);
    }

    private void callBody(QRuntime qRuntime, QLambda target) {
        try {
            QResult ifResult = target.call();
            qRuntime.cascadeReturn(ifResult);
            qRuntime.push(new DataValue(ifResult.getResult()));
        } catch (Exception e) {
            throw ThrowUtils.wrapException(e, errorReporter,
                    "IF_BODY_EXECUTE_ERROR", "if statement body execute error");
        }
    }
}
