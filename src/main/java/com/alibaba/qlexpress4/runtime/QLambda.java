package com.alibaba.qlexpress4.runtime;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface QLambda extends Runnable, Supplier<Object>, Consumer<Object>,
        Predicate<Object>, Function<Object, Object> {

    /**
     * @param params
     * @return
     * @throws Exception
     *         {@link com.alibaba.qlexpress4.exception.UserDefineException} for custom error message
     */
    QResult call(Object... params) throws Exception;

    @Override
    default Object get() {
        try {
            return call().getResult().get();
        } catch (Exception e) {
            throw e instanceof RuntimeException?
                    (RuntimeException) e: new RuntimeException(e);
        }
    }

    @Override
    default void accept(Object o) {
        try {
            call(o);
        } catch (Exception e) {
            throw e instanceof RuntimeException?
                    (RuntimeException) e: new RuntimeException(e);
        }
    }

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
