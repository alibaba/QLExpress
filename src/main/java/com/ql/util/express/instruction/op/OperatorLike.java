package com.ql.util.express.instruction.op;

import java.util.ArrayList;

import com.ql.util.express.Operator;

public class OperatorLike extends Operator {
    public OperatorLike(String name) {
        this.name = name;
    }

    public OperatorLike(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
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
        int i = 0, j = 0;
        int sLen = s.length(), pLen = pattern.length();
        int iStar = -1, jStar = -1;
        while (i < sLen) {
            if (j < pLen && (s.charAt(i) == pattern.charAt(j))) {
                i++;
                j++;
            } else if (j < pLen && pattern.charAt(j) == '%') {
                iStar = i;
                jStar = j;
                j++;
            } else if (iStar >= 0) {
                i = ++iStar;
                j = jStar + 1;
            } else {
                return false;
            }
        }
        while (j < pLen && pattern.charAt(j) == '%') {
            j++;
        }
        return j == pLen;
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
