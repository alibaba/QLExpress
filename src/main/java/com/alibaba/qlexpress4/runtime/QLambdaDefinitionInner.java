package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

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

    private final List<QLInstruction> instructionList;

    private final List<Param> paramsType;

    private final int maxStackSize;

    public QLambdaDefinitionInner(String name, List<QLInstruction> instructionList, List<Param> paramsType,
                                  int maxStackSize) {
        this.name = name;
        this.instructionList = instructionList;
        this.paramsType = paramsType;
        this.maxStackSize = maxStackSize;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<QLInstruction> getInstructionList() {
        return instructionList;
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
        return new QLambdaInner(this, qContext, qlOptions, newEnv);
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        for (QLInstruction qlInstruction : instructionList) {
            qlInstruction.println(depth, debug);
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
