package com.ql.util.express.instruction.op;

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
        if (op1 == null && op2 == null) {
            return true;
        }
        if (op1 == null || op2 == null) {
            return false;
        }
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
}
