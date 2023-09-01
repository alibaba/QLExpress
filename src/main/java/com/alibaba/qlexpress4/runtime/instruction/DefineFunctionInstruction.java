package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.function.QLambdaFunction;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: define function
 * @Input: 0
 * @Output: 0
 * <p>
 * Author: DQinYuan
 */
public class DefineFunctionInstruction extends QLInstruction {

    private final String name;

    private final QLambdaDefinition lambdaDefinition;

    public DefineFunctionInstruction(ErrorReporter errorReporter, String name, QLambdaDefinition lambdaDefinition) {
        super(errorReporter);
        this.name = name;
        this.lambdaDefinition = lambdaDefinition;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        QLambda lambda = lambdaDefinition.toLambda(qContext, qlOptions, true);
        qContext.defineFunction(name, new QLambdaFunction(lambda));
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
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "DefineFunction " + name, debug);
        lambdaDefinition.println(depth+1, debug);
    }
}
