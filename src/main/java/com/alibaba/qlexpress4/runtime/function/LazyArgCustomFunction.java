package com.alibaba.qlexpress4.runtime.function;

/**
 * A custom function that can control whether individual arguments are lazily evaluated.
 * Author: zimoa
 */
public interface LazyArgCustomFunction extends CustomFunction {
    /**
     * Determine whether the current argument should be evaluated lazily.
     *
     * @param argIndex zero-based argument index in the function call
     * @return true to delay evaluation
     */
    default boolean isLazyArg(int argIndex) {
        return true;
    }
}
