package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class QLambda implements Runnable, Predicate<Object>, Function<Object, Object> {

    private final String name;

    private final List<QLInstruction> instructionList;

    private final List<Class<?>> paramsType;

    public QLambda(String name, List<QLInstruction> instructionList, List<Class<?>> paramsType) {
        this.name = name;
        this.instructionList = instructionList;
        this.paramsType = paramsType;
    }

    public QResult call(Object... params) {
        return null;
    }

    @Override
    public void run() {
        call();
    }

    @Override
    public boolean test(Object o) {
        return (boolean) call(o).getResult().get();
    }

    @Override
    public Object apply(Object o) {
        return call(o).getResult().get();
    }
}
