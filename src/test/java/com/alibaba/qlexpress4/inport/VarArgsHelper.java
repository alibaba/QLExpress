package com.alibaba.qlexpress4.inport;

/**
 * A test helper class with varargs methods,
 * used to verify that MemberResolver handles varargs resolution correctly
 * when fewer arguments than required parameters are provided.
 */
public class VarArgsHelper {

    public static String format(String template, Object... args) {
        return String.format(template, args);
    }

    public static int sum(int required, int... rest) {
        int total = required;
        for (int r : rest) {
            total += r;
        }
        return total;
    }

    public static int sumAll(int... values) {
        int total = 0;
        for (int v : values) {
            total += v;
        }
        return total;
    }
}
