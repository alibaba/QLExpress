package com.ql.util.express.instruction.op;

import java.util.ArrayList;

import com.ql.util.express.Operator;

public class OperatorLike extends Operator {
    public OperatorLike(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) throws Exception {
        String s = op1.toString();
        String pattern = op2.toString();

        return matchPattern(s, pattern);
    }

    protected static boolean matchPattern(String s, String pattern) {
        int sPointer = 0, pPointer = 0;
        int sLen = s.length(), pLen = pattern.length();
        int sRecall = -1, pRecall = -1;
        while (sPointer < sLen) {
            if (pPointer < pLen && (s.charAt(sPointer) == pattern.charAt(pPointer))) {
                sPointer++;
                pPointer++;
            } else if (pPointer < pLen && pattern.charAt(pPointer) == '%') {
                sRecall = sPointer;
                pRecall = pPointer;
                pPointer++;
            } else if (sRecall >= 0) {
                sPointer = ++sRecall;
                pPointer = pRecall + 1;
            } else {
                return false;
            }
        }
        while (pPointer < pLen && pattern.charAt(pPointer) == '%') {
            pPointer++;
        }
        return pPointer == pLen;
    }

    public String[] split(String str, String s) {
        int start = 0;
        int end;
        String tmpStr;
        ArrayList<String> list = new ArrayList<>();
        do {
            end = str.indexOf(s, start);
            if (end < 0) {
                tmpStr = str.substring(start);
            } else {
                tmpStr = str.substring(start, end);
            }
            if (tmpStr.length() > 0) {
                list.add(tmpStr);
            }
            start = end + 1;
            if (start >= str.length()) {
                break;
            }
        } while (end >= 0);
        return list.toArray(new String[0]);
    }
}
