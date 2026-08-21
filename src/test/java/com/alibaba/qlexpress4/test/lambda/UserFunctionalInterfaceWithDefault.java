package com.alibaba.qlexpress4.test.lambda;

/**
 * A functional interface with a default method, used to test
 * that lambda proxies correctly delegate non-abstract methods.
 */
public interface UserFunctionalInterfaceWithDefault {

    int compute(int a, int b);

    default int computeWithOffset(int a, int b, int offset) {
        return compute(a, b) + offset;
    }
}
