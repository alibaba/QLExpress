package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;
import com.alibaba.qlexpress4.runtime.util.ValueUtils;

/**
 * @Operation: if top of stack is true, execute ${thenBody}, else execute ${elseBody}
 * @Input: 1
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class IfInstruction extends QLInstruction {

    private final QLambdaDefinition thenBody;

    private final QLambdaDefinition elseBody;

    private final boolean newEnv;

    public IfInstruction(ErrorReporter errorReporter, QLambdaDefinition thenBody, QLambdaDefinition elseBody,
                         boolean newEnv) {
        super(errorReporter);
        this.thenBody = thenBody;
        this.elseBody = elseBody;
        this.newEnv = newEnv;
    }

    @Override
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        Object condition = qRuntime.pop().get();
        if (!(condition instanceof Boolean)) {
            throw errorReporter.report("IF_CONDITION_NOT_BOOL",
                    "if condition expression result must be bool");
        }
        boolean conditionBool = (boolean) condition;
        QLambda lambda = (conditionBool? thenBody: elseBody)
                .toLambda(qRuntime, qlOptions, newEnv);
        return callBody(qRuntime, lambda);
    }

    @Override
    public int stackInput() {
        return 1;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    private QResult callBody(QRuntime qRuntime, QLambda target) {
        try {
            QResult ifResult = target.call();
            if (ifResult.getResultType() == QResult.ResultType.CASCADE_RETURN) {
                return ifResult;
            }
            qRuntime.push(ValueUtils.toImmutable(ifResult.getResult()));
            return QResult.CONTINUE_RESULT;
        } catch (Exception e) {
            throw ThrowUtils.wrapException(e, errorReporter,
                    "IF_BODY_EXECUTE_ERROR", "if statement body execute error");
        }
    }
}
