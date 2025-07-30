package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.exception.QLCompileException;

/**
 * 语法解析类
 * 1、单词分解
 *
 * @author xuannan
 */
public class WordSplit {
    enum EscapeState {
        CHARS,
        MAYBE_ESCAPE,
        END
    }

    private WordSplit() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 文本分析函数，“.”作为操作符号处理
     *
     * @param str String
     * @return String[]
     * @throws Exception
     */
    public static Word[] parse(String[] splitWord, String str) throws Exception {
        if (str == null) {
            return new Word[0];
        }
        char c;
        int line = 1;
        List<Word> list = new ArrayList<>();
        int i = 0;
        int point = 0;
        // 当前行第一个字符相对脚本起点的偏移量offset
        int currentLineOffset = 0;
        while (i < str.length()) {
            c = str.charAt(i);
            //字符串处理
            if (c == '"' || c == '\'') {
                StringBuilder escapedStr = new StringBuilder().append(c);
                EscapeState state = EscapeState.CHARS;
                int current = i;

                forward:
                while (++current < str.length()) {
                    char curChar = str.charAt(current);
                    switch (state) {
                        case CHARS:
                            if (curChar == '\\') {
                                state = EscapeState.MAYBE_ESCAPE;
                                continue;
                            }
                            escapedStr.append(curChar);
                            if (curChar == c) {
                                state = EscapeState.END;
                                break forward;
                            }

                            break;
                        case MAYBE_ESCAPE:
                            switch (curChar) {
                                case 'n':
                                    escapedStr.append('\n');
                                    break;
                                case 't':
                                    escapedStr.append('\t');
                                    break;
                                case 'r':
                                    escapedStr.append('\r');
                                    break;
                                default:
                                    escapedStr.append(curChar);
                            }
                            state = EscapeState.CHARS;
                    }
                }

                if (state != EscapeState.END) {
                    throw new QLCompileException("字符串没有关闭");
                }
                list.add(new Word(escapedStr.toString(), line, i - currentLineOffset + 1));
                i = current + 1;
                point = i;
            } else if (c == '.' && point < i && isNumber(str.substring(point, i))) {
                //小数点的特殊处理
                i = i + 1;
            } else if (c == ' ' || c == '\r' || c == '\n' || c == '\t' || c == '\u000C') {
                if (point < i) {
                    list.add(new Word(str.substring(point, i), line, point - currentLineOffset + 1));
                }
                if (c == '\n') {
                    line = line + 1;
                    currentLineOffset = i + 1;
                }
                i = i + 1;
                point = i;
            } else {
                boolean isFind = false;
                for (String s : splitWord) {
                    int length = s.length();
                    if (i + length <= str.length() && str.substring(i, i + length).equals(s)) {
                        if (point < i) {
                            list.add(new Word(str.substring(point, i), line, point - currentLineOffset + 1));
                        }
                        list.add(new Word(str.substring(i, i + length), line, i - currentLineOffset + 1));
                        i = i + length;
                        point = i;
                        isFind = true;
                        break;
                    }
                }
                if (!isFind) {
                    i = i + 1;
                }
            }
        }
        if (point < i) {
            list.add(new Word(str.substring(point, i), line, point - currentLineOffset + 1));
        }

        Word[] result = new Word[list.size()];
        list.toArray(result);
        return result;
    }

    public static void sortSplitWord(String[] splitWord) {
        Arrays.sort(splitWord, (o1, o2) -> Integer.compare(o2.length(), o1.length()));
    }

    protected static boolean isNumber(String str) {
        return ExpressUtil.isNumber(str);
    }

    public static String getPrintInfo(Object[] list, String splitOp) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < list.length; i++) {
            if (i > 0) {
                buffer.append(splitOp);
            }
            buffer.append("{").append(list[i]).append("}");
        }
        return buffer.toString();
    }
}
