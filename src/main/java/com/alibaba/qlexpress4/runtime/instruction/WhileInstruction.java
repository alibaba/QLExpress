package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;

/**
 * @Operation: while (condition) do body
 * @Input: 0
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class WhileInstruction extends QLInstruction {

    private final QLambda condition;

    private final QLambda body;

    public WhileInstruction(ErrorReporter errorReporter, QLambda condition, QLambda body) {
        super(errorReporter);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        whileBody:
        while (evalCondition()) {
            try {
                QResult bodyResult = body.call();
                switch (bodyResult.getResultType()) {
                    case RETURN:
                        qRuntime.exitAndReturn(bodyResult);
                        break;
                    case BREAK:
                        break whileBody;
                }
            } catch (Exception e) {
                throw ThrowUtils.wrapException(e, errorReporter,
                        "WHILE_BODY_EXECUTE_ERROR", "while body execute error");
            }
        }
    }

    private boolean evalCondition() {
        try {
            Object conditionResult = condition.call().getResult().get();
            if (!(conditionResult instanceof Boolean)) {
                throw errorReporter.report("WHILE_CONDITION_NOT_BOOL",
                        "while condition must be bool");
            }
            return (boolean) conditionResult;
        } catch (Exception e) {
            throw ThrowUtils.wrapException(e, errorReporter, "WHILE_CONDITION_EVAL_ERROR",
                    "while statement condition evaluate error");
        }
    }
}
