package com.alibaba.qlexpress4.utils;

public class QLStringUtils {
    
    public static String parseStringEscape(String originStr) {
        return parseStringEscapeStartEnd(originStr, 1, originStr.length() - 1);
    }
    
    public static String parseStringEscapeStartEnd(String originStr, int start, int end) {
        StringBuilder result = new StringBuilder();
        final byte init = 0;
        final byte escape = 1;
        byte state = 0;
        
        int i = start;
        while (i < end) {
            char cur = originStr.charAt(i++);
            switch (state) {
                case init:
                    if (cur == '\\') {
                        state = escape;
                    }
                    else {
                        result.append(cur);
                    }
                    break;
                case escape:
                    state = init;
                    switch (cur) {
                        case 'b':
                            result.append('\b');
                            break;
                        case 't':
                            result.append('\t');
                            break;
                        case 'n':
                            result.append('\n');
                            break;
                        case 'f':
                            result.append('\f');
                            break;
                        case 'r':
                            result.append('\r');
                            break;
                        case '"':
                            result.append('"');
                            break;
                        case '\'':
                            result.append('\'');
                            break;
                        case '\\':
                            result.append('\\');
                            break;
                        case '$':
                            result.append('$');
                            break;
                    }
                    break;
            }
        }
        return result.toString();
    }
    
}
