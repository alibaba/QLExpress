package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: instantiate lambda definition on stack
 * Input: 0
 * Output: 1 lambda instance
 *
 * Author: DQinYuan
 */
public class LoadLambdaInstruction extends QLInstruction {

    private final QLambdaDefinition lambdaDefinition;

    public LoadLambdaInstruction(ErrorReporter errorReporter, QLambdaDefinition lambdaDefinition) {
        super(errorReporter);
        this.lambdaDefinition = lambdaDefinition;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        QLambda lambdaInstance = lambdaDefinition.toLambda(qContext, qlOptions, true);
        qContext.push(new DataValue(lambdaInstance));
        return QResult.NEXT_INSTRUCTION;
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
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": LoadLambda", debug);
        lambdaDefinition.println(depth+1, debug);
    }

    public QLambdaDefinition getLambdaDefinition() {
        return lambdaDefinition;
    }
}
