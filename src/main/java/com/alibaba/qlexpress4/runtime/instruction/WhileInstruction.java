package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QLambdaDefinition;
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

    private final QLambdaDefinition condition;

    private final QLambdaDefinition body;

    public WhileInstruction(ErrorReporter errorReporter, QLambdaDefinition condition, QLambdaDefinition body) {
        super(errorReporter);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        whileBody:
        while (evalCondition(qRuntime, qlOptions)) {
            try {
                QLambda bodyLambda = body.toLambda(qRuntime, qlOptions, true);
                QResult bodyResult = bodyLambda.call();
                switch (bodyResult.getResultType()) {
                    case CASCADE_RETURN:
                        return bodyResult;
                    case BREAK:
                        break whileBody;
                }
            } catch (Exception e) {
                throw ThrowUtils.wrapException(e, errorReporter,
                        "WHILE_BODY_EXECUTE_ERROR", "while body execute error");
            }
        }
        return QResult.CONTINUE_RESULT;
    }

    private boolean evalCondition(QRuntime qRuntime, QLOptions qlOptions) {
        try {
            QLambda conditionLambda = condition.toLambda(qRuntime, qlOptions, false);
            Object conditionResult = conditionLambda.call().getResult().get();
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
