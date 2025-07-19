package com.alibaba.qlexpress4.utils;

import java.util.function.Consumer;

/**
 * Author: DQinYuan
 */
public class PrintlnUtils {
    
    public static void printlnByCurDepth(int depth, String str, Consumer<String> debug) {
        debug.accept(buildIndentString(depth, str));
    }
    
    public static String buildIndentString(int indent, String originStr) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            if (i == indent - 1) {
                builder.append("| ");
            }
            else {
                builder.append("  ");
            }
        }
        builder.append(originStr);
        return builder.toString();
    }
    
}
