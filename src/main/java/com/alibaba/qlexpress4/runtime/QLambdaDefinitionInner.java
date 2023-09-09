package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.runtime.instruction.CloseScopeInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewScopeInstruction;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.List;
import java.util.function.Consumer;

/**
 * Author: DQinYuan
 */
public class QLambdaDefinitionInner implements QLambdaDefinition {

    /**
     * function name
     */
    private final String name;

    private final QLInstruction[] instructions;

    private final List<Param> paramsType;

    private final int maxStackSize;

    public QLambdaDefinitionInner(String name, List<QLInstruction> instructions, List<Param> paramsType,
                                  int maxStackSize) {
        this.name = name;
        this.instructions = instructions.toArray(new QLInstruction[0]);
        this.paramsType = paramsType;
        this.maxStackSize = maxStackSize;
    }

    public QLambdaDefinitionInner(String name, QLInstruction[] instructions, List<Param> paramsType,
                                  int maxStackSize) {
        this.name = name;
        this.instructions = instructions;
        this.paramsType = paramsType;
        this.maxStackSize = maxStackSize;
    }

    @Override
    public String getName() {
        return name;
    }

    public QLInstruction[] getInstructions() {
        return instructions;
    }

    public List<Param> getParamsType() {
        return paramsType;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public QLambda toLambda(QContext qContext, QLOptions qlOptions,
                            boolean newEnv) {
        return new QLambdaInner(this, new DelegateQContext(qContext, qContext.getCurrentScope()),
                qlOptions, newEnv);
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        for (int i = 0; i < instructions.length; i++) {
            instructions[i].println(i, depth, debug);
        }
    }

    public static class Param {
        private final String name;
        private final Class<?> clazz;

        public Param(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public String getName() {
            return name;
        }

        public Class<?> getClazz() {
            return clazz;
        }
    }
}
