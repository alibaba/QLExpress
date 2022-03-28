package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.List;

public class QLambda {

    private final String name;

    private final List<QLInstruction> instructionList;

    private final List<Class<?>> paramsType;

    public QLambda(String name, List<QLInstruction> instructionList, List<Class<?>> paramsType) {
        this.name = name;
        this.instructionList = instructionList;
        this.paramsType = paramsType;
    }

    public Object call(Object... params) {
        return null;
    }
}
