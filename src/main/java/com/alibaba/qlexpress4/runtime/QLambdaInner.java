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

    private final List<Class<?>> paramsType;

    public QLambdaInner(QVm qVm, String name, List<QLInstruction> instructionList, List<Class<?>> paramsType) {
        this.qVm = qVm;
        this.name = name;
        this.instructionList = instructionList;
        this.paramsType = paramsType;
    }

    public QResult call(Object... params) {
        return null;
    }

}
