package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: while (condition) do body
 * @Input: 0
 * @Output: 0
 * <p>
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
    public QResult execute(int index, QContext qContext, QLOptions qlOptions) {
        whileBody:
        while (evalCondition(qContext, qlOptions)) {
            try {
                QLambda bodyLambda = body.toLambda(qContext, qlOptions, true);
                QResult bodyResult = bodyLambda.call();
                switch (bodyResult.getResultType()) {
                    case RETURN:
                        return bodyResult;
                    case BREAK:
                        break whileBody;
                }
            } catch (Throwable t) {
                throw ThrowUtils.wrapThrowable(t, errorReporter,
                        "WHILE_BODY_EXECUTE_ERROR", "while body execute error");
            }
        }
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
        PrintlnUtils.printlnByCurDepth(index, depth, "While", debug);
        PrintlnUtils.printlnByCurDepth(index, depth+1, "Condition", debug);
        condition.println(depth+2, debug);
        PrintlnUtils.printlnByCurDepth(index, depth+1, "Body", debug);
        body.println(depth+2, debug);
    }

    private boolean evalCondition(QContext qContext, QLOptions qlOptions) {
        try {
            QLambda conditionLambda = condition.toLambda(qContext, qlOptions, false);
            Object conditionResult = conditionLambda.call().getResult().get();
            if (!(conditionResult instanceof Boolean)) {
                throw errorReporter.report("WHILE_CONDITION_NOT_BOOL",
                        "condition must be bool");
            }
            return (boolean) conditionResult;
        } catch (Throwable t) {
            throw ThrowUtils.wrapThrowable(t, errorReporter, "CONDITION_EVAL_ERROR",
                    "condition evaluate error");
        }
    }
}
