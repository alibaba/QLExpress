package com.alibaba.qlexpress4.runtime.operator.string;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * Author: DQinYuan
 */
public class LikeOperator extends BaseBinaryOperator {

    private static final LikeOperator INSTANCE = new LikeOperator();

    public static BinaryOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public Object execute(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter) {
        Object target = left.get();
        Object pattern = right.get();
        if (target == null || pattern == null) {
            return false;
        }
        if (!(target instanceof String) || !(pattern instanceof String)) {
            throw buildInvalidOperandTypeException(left, right, errorReporter);
        }
        return matchPattern((String) target, (String) pattern);
    }

    private static boolean matchPattern(String s, String pattern) {
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

    @Override
    public String getOperator() {
        return "like";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.IN_LIKE;
    }
}
