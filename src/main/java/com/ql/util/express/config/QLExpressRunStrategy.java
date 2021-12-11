package com.ql.util.express.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.exception.QLSecurityRiskException;

/**
 * ExpressRunner设置全局生效的配置，直接使用静态方法控制
 */
public class QLExpressRunStrategy {
    /**
     * 预防空指针
     */
    private static boolean avoidNullPointer = false;

    /**
     * 当空对象进行大小比较时，返回false, 例如 1 > null 和 null > 1都返回false
     */
    private static boolean compareNullLessMoreAsFalse = false;

    /**
     * 禁止调用不安全的方法
     */
    private static boolean forbidInvokeSecurityRiskMethods = false;

    private static final List<String> SECURITY_RISK_METHOD_LIST = new ArrayList<>();

    static {
        //系统退出
        SECURITY_RISK_METHOD_LIST.add(System.class.getName() + "." + "exit");

        //运行脚本命令
        SECURITY_RISK_METHOD_LIST.add(Runtime.getRuntime().getClass().getName() + ".exec");
    }

    public static boolean isCompareNullLessMoreAsFalse() {
        return compareNullLessMoreAsFalse;
    }

    public static void setCompareNullLessMoreAsFalse(boolean compareNullLessMoreAsFalse) {
        QLExpressRunStrategy.compareNullLessMoreAsFalse = compareNullLessMoreAsFalse;
    }

    public static boolean isAvoidNullPointer() {
        return avoidNullPointer;
    }

    public static void setAvoidNullPointer(boolean avoidNullPointer) {
        QLExpressRunStrategy.avoidNullPointer = avoidNullPointer;
    }

    public static boolean isForbidInvokeSecurityRiskMethods() {
        return forbidInvokeSecurityRiskMethods;
    }

    public static void setForbidInvokeSecurityRiskMethods(boolean forbidInvokeSecurityRiskMethods) {
        QLExpressRunStrategy.forbidInvokeSecurityRiskMethods = forbidInvokeSecurityRiskMethods;
    }

    /**
     * TODO 未考虑方法重载的场景
     *
     * @param clazz
     * @param methodName
     */
    public static void addSecurityRiskMethod(Class<?> clazz, String methodName) {
        QLExpressRunStrategy.SECURITY_RISK_METHOD_LIST.add(clazz.getName() + "." + methodName);
    }

    public static void assertSecurityRiskMethod(Method method) throws QLSecurityRiskException {
        if (!forbidInvokeSecurityRiskMethods || method == null) {
            return;
        }

        String fullMethodName = method.getDeclaringClass().getName() + "." + method.getName();
        if (SECURITY_RISK_METHOD_LIST.contains(fullMethodName)) {
            throw new QLSecurityRiskException("使用QLExpress调用了不安全的系统方法:" + method);
        }
    }
}
