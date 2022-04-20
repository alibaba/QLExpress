package com.alibaba.qlexpress4.runtime;

import java.util.function.Function;
import java.util.function.Predicate;

public interface QLambda extends Runnable, Predicate<Object>, Function<Object, Object> {

    QResult call(Object... params);

    @Override
    default void run() {
        call();
    }

    @Override
    default boolean test(Object o) {
        return (boolean) call(o).getResult().get();
    }

    @Override
    default Object apply(Object o) {
        return call(o).getResult().get();
    }
}
