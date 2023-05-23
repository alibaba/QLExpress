package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.member.IMethod;

import java.util.List;

/**
 * @Author TaoKan
 * @Date 2023/4/27 下午3:32
 */
public class SecurityUtils {
    private static void checkSafePointBlackList(QLOptions qlOptions, ErrorReporter errorReporter,
                                                IMethod iMethod) throws QLRuntimeException {
        if (qlOptions.getSafePointStrategy().checkBlackList().checkRulesPassed(iMethod)) {
            throw errorReporter.report("BLACK_LIST_CHECK_ERROR", "found method:" +
                    iMethod.getQualifyName() + " in blacklist rules");
        }
    }

    private static void checkSafePointWhiteList(QLOptions qlOptions, ErrorReporter errorReporter,
                                                IMethod iMethod) throws QLRuntimeException {
        if (!qlOptions.getSafePointStrategy().checkWhiteList().checkRulesPassed(iMethod)) {
            throw errorReporter.report("WHITE_LIST_CHECK_ERROR", "found method:" +
                    iMethod.getQualifyName() + " not in whitelist rules");
        }
    }

    public static void checkSafePointStrategyList(QLOptions qlOptions, ErrorReporter errorReporter,
                                                  IMethod iMethod) throws QLRuntimeException {
        if (qlOptions.getSafePointStrategy().checkWhiteList() != null) {
            checkSafePointBlackList(qlOptions, errorReporter, iMethod);
        }
        if (qlOptions.getSafePointStrategy().checkWhiteList() != null) {
            checkSafePointWhiteList(qlOptions, errorReporter, iMethod);
        }
    }

    public static void checkSafePointStrategyList(QLOptions qlOptions, ErrorReporter errorReporter,
                                                  List<IMethod> iMethods) throws QLRuntimeException {
        if (qlOptions.getSafePointStrategy().checkWhiteList() != null) {
            iMethods.forEach((x) -> checkSafePointBlackList(qlOptions, errorReporter, x));
        }
        if (qlOptions.getSafePointStrategy().checkWhiteList() != null) {
            iMethods.forEach((x) -> checkSafePointWhiteList(qlOptions, errorReporter, x));
        }
    }
}
