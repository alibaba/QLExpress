package com.alibaba.qlexpress4.runtime;

import java.util.function.Function;
import java.util.function.Predicate;

public interface QLambda extends Runnable, Predicate<Object>, Function<Object, Object> {

    QResult call(Object... params) throws Exception;

    @Override
    default void run() {
        try {
            call();
        } catch (Exception e) {
            throw e instanceof RuntimeException?
                    (RuntimeException) e: new RuntimeException(e);
        }
    }

    @Override
    default boolean test(Object o) {
        try {
            return (boolean) call(o).getResult().get();
        } catch (Exception e) {
            throw e instanceof RuntimeException?
                    (RuntimeException) e: new RuntimeException(e);
        }
    }

    @Override
    default Object apply(Object o) {
        try {
            return call(o).getResult().get();
        } catch (Exception e) {
            throw e instanceof RuntimeException?
                    (RuntimeException) e: new RuntimeException(e);
        }
    }
}
