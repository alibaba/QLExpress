package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

public class OperatorLike extends Operator {
    public static final char PERCENT_SIGN = '%';

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
        return likeMatch(op1.toString(),op2.toString());
    }

    public boolean likeMatch(String dest, String pattern){
        int destPointer = 0, patternPointer = 0;
        int destLen = dest.length(), patternLen = pattern.length();
        int destRecall = -1, patternRecall = -1;
        while (destPointer < destLen) {
            if (patternPointer < patternLen && (dest.charAt(destPointer) == pattern.charAt(patternPointer))) {
                destPointer++;
                patternPointer++;
            } else if (patternPointer < patternLen && pattern.charAt(patternPointer) == PERCENT_SIGN) {
                destRecall = destPointer;
                patternRecall = patternPointer;
                patternPointer++;
            } else if (destRecall >= 0) {
                destPointer = ++destRecall;
                patternPointer = patternRecall + 1;
            } else {
                return false;
            }
        }
        while (patternPointer < patternLen && pattern.charAt(patternPointer) == PERCENT_SIGN) {
            patternPointer++;
        }
        return patternPointer == patternLen;
    }
}
