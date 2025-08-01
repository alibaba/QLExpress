package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.scope.QvmBlockScope;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Operation: traditional for loop
 * Input: 0
 * Output: 0
 * Author: DQinYuan
 */
public class ForInstruction extends QLInstruction {
    
    /**
     * nullable
     */
    private final QLambdaDefinition forInit;
    
    /**
     * nullable
     */
    private final QLambdaDefinition condition;
    
    private final ErrorReporter conditionErrorReporter;
    
    /**
     * nullable
     */
    private final QLambdaDefinition forUpdate;
    
    private final int forScopeMaxStackSize;
    
    private final QLambdaDefinition forBody;
    
    public ForInstruction(ErrorReporter errorReporter, QLambdaDefinition forInit, QLambdaDefinition condition,
        ErrorReporter conditionErrorReporter, QLambdaDefinition forUpdate, int forScopeMaxStackSize,
        QLambdaDefinition forBody) {
        super(errorReporter);
        this.forInit = forInit;
        this.condition = condition;
        this.conditionErrorReporter = conditionErrorReporter;
        this.forUpdate = forUpdate;
        this.forScopeMaxStackSize = forScopeMaxStackSize;
        this.forBody = forBody;
    }
    
    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        QContext forScopeContext =
            needForScope()
                ? new DelegateQContext(qContext,
                    new QvmBlockScope(qContext, new HashMap<>(1), forScopeMaxStackSize, ExceptionTable.EMPTY))
                : qContext;
        if (forInit != null) {
            QLambda initLambda = forInit.toLambda(forScopeContext, qlOptions, false);
            try {
                initLambda.call();
            }
            catch (Throwable t) {
                throw ThrowUtils.wrapThrowable(t,
                    errorReporter,
                    QLErrorCodes.FOR_INIT_ERROR.name(),
                    QLErrorCodes.FOR_INIT_ERROR.getErrorMsg());
            }
        }
        
        QLambda conditionLambda = condition != null ? condition.toLambda(forScopeContext, qlOptions, false) : null;
        QLambda updateLambda = forUpdate != null ? forUpdate.toLambda(forScopeContext, qlOptions, false) : null;
        QLambda bodyLambda = forBody.toLambda(forScopeContext, qlOptions, true);
        
        forBody: while (conditionLambda == null || evalCondition(conditionLambda)) {
            try {
                QResult bodyResult = bodyLambda.call();
                switch (bodyResult.getResultType()) {
                    case RETURN:
                        return bodyResult;
                    case BREAK:
                        break forBody;
                }
            }
            catch (Throwable t) {
                throw ThrowUtils.wrapThrowable(t,
                    errorReporter,
                    QLErrorCodes.FOR_BODY_ERROR.name(),
                    QLErrorCodes.FOR_BODY_ERROR.getErrorMsg());
            }
            if (updateLambda != null) {
                runUpdate(updateLambda);
            }
        }
        return QResult.NEXT_INSTRUCTION;
    }
    
    private boolean needForScope() {
        return forInit != null || condition != null || forUpdate != null;
    }
    
    private void runUpdate(QLambda updateLambda) {
        try {
            updateLambda.call();
        }
        catch (Throwable t) {
            throw ThrowUtils.wrapThrowable(t,
                errorReporter,
                QLErrorCodes.FOR_UPDATE_ERROR.name(),
                QLErrorCodes.FOR_UPDATE_ERROR.getErrorMsg());
        }
    }
    
    private boolean evalCondition(QLambda conditionLambda) {
        try {
            Object conditionResult = conditionLambda.call().getResult().get();
            if (!(conditionResult instanceof Boolean)) {
                throw conditionErrorReporter.report(QLErrorCodes.FOR_CONDITION_BOOL_REQUIRED.name(),
                    QLErrorCodes.FOR_CONDITION_BOOL_REQUIRED.getErrorMsg());
            }
            return (boolean)conditionResult;
        }
        catch (Throwable t) {
            throw ThrowUtils.wrapThrowable(t,
                conditionErrorReporter,
                QLErrorCodes.FOR_CONDITION_ERROR.name(),
                QLErrorCodes.FOR_CONDITION_ERROR.getErrorMsg());
        }
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
        PrintlnUtils.printlnByCurDepth(depth, index + ": For", debug);
        PrintlnUtils.printlnByCurDepth(depth + 1, "Init", debug);
        if (forInit != null) {
            forInit.println(depth + 2, debug);
        }
        PrintlnUtils.printlnByCurDepth(depth + 1, "Condition", debug);
        if (condition != null) {
            condition.println(depth + 2, debug);
        }
        PrintlnUtils.printlnByCurDepth(depth + 1, "Update", debug);
        if (forUpdate != null) {
            forUpdate.println(depth + 2, debug);
        }
        PrintlnUtils.printlnByCurDepth(depth + 1, "Body", debug);
        forBody.println(depth + 2, debug);
    }
}
