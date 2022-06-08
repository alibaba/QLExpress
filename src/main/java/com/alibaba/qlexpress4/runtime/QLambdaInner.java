package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.List;

/**
 * Author: DQinYuan
 */
public class QLambdaInner implements QLambda {

    private final QVm qVm;

    /**
     * function name
     */
    private final String name;

    private final List<QLInstruction> instructionList;

    private final List<Param> paramsType;

    public QLambdaInner(QVm qVm, String name, List<QLInstruction> instructionList, List<Param> paramsType) {
        this.qVm = qVm;
        this.name = name;
        this.instructionList = instructionList;
        this.paramsType = paramsType;
    }

    public QResult call(Object... params) throws Exception {
        return null;
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
