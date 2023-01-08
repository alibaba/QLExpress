package com.alibaba.qlexpress4.utils;

import java.util.function.Consumer;

/**
 * Author: DQinYuan
 */
public class PrintlnUtils {

    public static void printlnByCurDepth(int index, int depth, String str, Consumer<String> debug) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            if (i == depth - 1) {
                builder.append("| ");
            } else {
                builder.append("  ");
            }
        }
        builder.append(index == -1? "": String.valueOf(index))
                .append(" ")
                .append(str);
        debug.accept(builder.toString());
    }

}
