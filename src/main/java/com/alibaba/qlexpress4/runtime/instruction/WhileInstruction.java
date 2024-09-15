package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.scope.QvmBlockScope;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Operation: while (condition) do body
 * Input: 0
 * Output: 0
 * <p>
 * Author: DQinYuan
 */
public class WhileInstruction extends QLInstruction {

    private final QLambdaDefinition condition;

    private final QLambdaDefinition body;

    private final int whileScopeMaxStackSize;

    public WhileInstruction(ErrorReporter errorReporter, QLambdaDefinition condition, QLambdaDefinition body,
                            int whileScopeMaxStackSize) {
        super(errorReporter);
        this.condition = condition;
        this.body = body;
        this.whileScopeMaxStackSize = whileScopeMaxStackSize;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        DelegateQContext whileScopeContext = new DelegateQContext(qContext,
                new QvmBlockScope(qContext, Collections.emptyMap(), whileScopeMaxStackSize, ExceptionTable.EMPTY));
        QLambda conditionLambda = condition.toLambda(whileScopeContext, qlOptions, false);
        QLambda bodyLambda = body.toLambda(whileScopeContext, qlOptions, true);
        whileBody:
        while (evalCondition(conditionLambda)) {
            try {
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
        PrintlnUtils.printlnByCurDepth(depth, index + ": While", debug);
        PrintlnUtils.printlnByCurDepth(depth+1, "Condition", debug);
        condition.println(depth+2, debug);
        PrintlnUtils.printlnByCurDepth(depth+1, "Body", debug);
        body.println(depth+2, debug);
    }

    private boolean evalCondition(QLambda conditionLambda) {
        try {
            Object conditionResult = conditionLambda.call().getResult().get();
            if (!(conditionResult instanceof Boolean)) {
                throw errorReporter.report(QLErrorCodes.WHILE_CONDITION_BOOL_REQUIRED.name(),
                        QLErrorCodes.WHILE_CONDITION_BOOL_REQUIRED.getErrorMsg());
            }
            return (boolean) conditionResult;
        } catch (Throwable t) {
            throw ThrowUtils.wrapThrowable(t, errorReporter, QLErrorCodes.WHILE_CONDITION_ERROR.name(),
                    QLErrorCodes.WHILE_CONDITION_ERROR.getErrorMsg());
        }
    }
}
