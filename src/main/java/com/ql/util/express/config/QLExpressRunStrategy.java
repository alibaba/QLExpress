package com.ql.util.express.config;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ql.util.express.config.whitelist.WhiteChecker;
import com.ql.util.express.exception.QLSecurityRiskException;

/**
 * ExpressRunner设置全局生效的配置，直接使用静态方法控制
 */
public class QLExpressRunStrategy {
    /**
     * 沙箱模式开关
     */
    private static boolean sandboxMode = false;
    /**
     * 编译期类型白名单
     * null 表示不进行校验
     * 如果编译时发现引用了白名单之外的类, 就会抛出异常
     */
    private static List<WhiteChecker> compileWhiteCheckerList = null;

    /**
     * 预防空指针
     */
    private static boolean avoidNullPointer = false;

    /**
     * 当空对象进行大小比较时，返回false, 例如 1 > null 和 null > 1都返回false
     */
    private static boolean compareNullLessMoreAsFalse = false;

    private static ClassLoader customClassLoader = null;

    /**
     * 禁止调用不安全的方法
     */
    private static boolean forbidInvokeSecurityRiskMethods = false;

    /**
     * 黑名单控制
     */
    private static final Set<String> SECURITY_RISK_METHOD_LIST = new HashSet<>();

    /**
     * 白名单控制
     */
    private static Set<String> SECURE_METHOD_LIST = new HashSet<>();

    /**
     * 最大申请的数组大小, 默认没有限制
     * 防止用户一次性申请过多的内存
     * -1 表示没有限制
     */
    private static int maxArrLength = -1;

    static {
        // 系统退出
        SECURITY_RISK_METHOD_LIST.add(System.class.getName() + "." + "exit");

        // 运行脚本命令
        SECURITY_RISK_METHOD_LIST.add(Runtime.getRuntime().getClass().getName() + ".exec");
        SECURITY_RISK_METHOD_LIST.add(ProcessBuilder.class.getName() + ".start");

        // 反射相关
        SECURITY_RISK_METHOD_LIST.add(Method.class.getName() + ".invoke");
        SECURITY_RISK_METHOD_LIST.add(Class.class.getName() + ".forName");
        SECURITY_RISK_METHOD_LIST.add(ClassLoader.class.getName() + ".loadClass");
        SECURITY_RISK_METHOD_LIST.add(ClassLoader.class.getName() + ".findClass");
        SECURITY_RISK_METHOD_LIST.add(ClassLoader.class.getName() + ".defineClass");
        SECURITY_RISK_METHOD_LIST.add(ClassLoader.class.getName() + ".getSystemClassLoader");

        // jndi 相关
        SECURITY_RISK_METHOD_LIST.add("javax.naming.InitialContext.lookup");
        SECURITY_RISK_METHOD_LIST.add("com.sun.rowset.JdbcRowSetImpl.setDataSourceName");
        SECURITY_RISK_METHOD_LIST.add("com.sun.rowset.JdbcRowSetImpl.setAutoCommit");

        SECURITY_RISK_METHOD_LIST.add("jdk.jshell.JShell.create");
        SECURITY_RISK_METHOD_LIST.add("javax.script.ScriptEngineManager.getEngineByName");
        SECURITY_RISK_METHOD_LIST.add("org.springframework.jndi.JndiLocatorDelegate.lookup");

        // QLE QLExpressRunStrategy的所有方法
        for (Method method : QLExpressRunStrategy.class.getMethods()) {
            SECURITY_RISK_METHOD_LIST.add(QLExpressRunStrategy.class.getName() + "." + method.getName());
        }
    }

    private QLExpressRunStrategy() {
        throw new IllegalStateException("Utility class");
    }

    public static void setSandBoxMode(boolean sandboxMode) {
        QLExpressRunStrategy.sandboxMode = sandboxMode;
    }

    public static boolean isSandboxMode() {
        return sandboxMode;
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

    public static ClassLoader getCustomClassLoader() {
        return customClassLoader;
    }

    public static void setCustomClassLoader(ClassLoader customClassLoader) {
        QLExpressRunStrategy.customClassLoader = customClassLoader;
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

    public static void setSecureMethods(Set<String> secureMethods) {
        SECURE_METHOD_LIST = secureMethods;
    }

    public static void addSecureMethod(Class<?> clazz, String methodName) {
        SECURE_METHOD_LIST.add(clazz.getName() + "." + methodName);
    }

    public static void assertSecurityRiskMethod(Method method) throws QLSecurityRiskException {
        if (!forbidInvokeSecurityRiskMethods || method == null) {
            return;
        }

        String fullMethodName = method.getDeclaringClass().getName() + "." + method.getName();
        if (SECURE_METHOD_LIST != null && !SECURE_METHOD_LIST.isEmpty()) {
            // 有白名单配置时则黑名单失效
            if (!SECURE_METHOD_LIST.contains(fullMethodName)) {
                throw new QLSecurityRiskException("使用QLExpress调用了不安全的系统方法:" + method);
            }
            return;
        }

        if (SECURITY_RISK_METHOD_LIST.contains(fullMethodName)) {
            throw new QLSecurityRiskException("使用QLExpress调用了不安全的系统方法:" + method);
        }
    }

    /**
     * @param clazz
     * @return true 表示位于白名单中, false 表示不在白名单中
     */
    public static boolean checkWhiteClassList(Class<?> clazz) {
        if (compileWhiteCheckerList == null) {
            return true;
        }
        for (WhiteChecker whiteChecker : compileWhiteCheckerList) {
            if (whiteChecker.check(clazz)) {
                return true;
            }
        }
        return false;
    }

    public static void setCompileWhiteCheckerList(List<WhiteChecker> compileWhiteCheckerList) {
        QLExpressRunStrategy.compileWhiteCheckerList = compileWhiteCheckerList;
    }

    public static void setMaxArrLength(int maxArrLength) {
        QLExpressRunStrategy.maxArrLength = maxArrLength;
    }

    public static boolean checkArrLength(int arrLen) {
        return QLExpressRunStrategy.maxArrLength == -1 || arrLen <= QLExpressRunStrategy.maxArrLength;
    }
}
