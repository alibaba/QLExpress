package com.alibaba.qlexpress4.runtime;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface QLambda
    extends Runnable, Supplier<Object>, Consumer<Object>, Predicate<Object>, Function<Object, Object> {

    /**
     * @param params
     * @return
     * @throws Throwable {@link com.alibaba.qlexpress4.exception.UserDefineException} for custom error message
     */
    QResult call(Object... params) throws Throwable;

    @Override
    default Object get() {
        try {
            return call().getResult().get();
        } catch (Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
        }
    }

    @Override
    default void accept(Object o) {
        try {
            call(o);
        } catch (Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
        }
    }

    @Override
    default void run() {
        try {
            call();
        } catch (Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
        }
    }

    @Override
    default boolean test(Object o) {
        try {
            return (boolean)call(o).getResult().get();
        } catch (Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
        }
    }

    @Override
    default Object apply(Object o) {
        try {
            return call(o).getResult().get();
        } catch (Throwable t) {
            throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
        }
    }
}
