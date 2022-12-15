package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;
import com.alibaba.qlexpress4.runtime.util.ValueUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: call const lambda
 * @Input: 0
 * @Output: 1 const lambda result
 * <p>
 * Author: DQinYuan
 */
public class CallConstInstruction extends QLInstruction {

    private final QLambdaDefinition constLambda;

    public CallConstInstruction(ErrorReporter errorReporter, QLambdaDefinition constLambda) {
        super(errorReporter);
        this.constLambda = constLambda;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        QLambda lambda = constLambda.toLambda(qContext, qlOptions, true);
        try {
            QResult result = lambda.call();
            if (QResult.ResultType.CASCADE_RETURN == result.getResultType()) {
                return result;
            }
            qContext.push(ValueUtils.toImmutable(result.getResult()));
            return QResult.NEXT_INSTRUCTION;
        } catch (Exception e) {
            throw ThrowUtils.wrapException(e, errorReporter, "BLOCK_EXECUTE_ERROR", "block execute error");
        }
    }

    @Override
    public int stackInput() {
        return 0;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "CallConst " + constLambda.getName(), debug);
        constLambda.println(depth+1, debug);
    }
}
