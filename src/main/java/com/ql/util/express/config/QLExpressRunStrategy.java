package com.ql.util.express.config;

import com.ql.util.express.exception.QLSecurityRiskException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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


    /**
     * 禁止调用不安全的方法
     */
    private static boolean forbiddenInvokeSecurityRiskMethods = false;

    public static boolean isForbiddenInvokeSecurityRiskMethods() {
        return forbiddenInvokeSecurityRiskMethods;
    }

    public static void setForbiddenInvokeSecurityRiskMethods(boolean forbiddenInvokeSecurityRiskMethods) {
        QLExpressRunStrategy.forbiddenInvokeSecurityRiskMethods = forbiddenInvokeSecurityRiskMethods;
    }

    private static List<String>securityRiskMethods = new ArrayList<String>();

    static{
        securityRiskMethods.add(System.class.getName()+"."+"exit");//系统退出
        securityRiskMethods.add(Runtime.getRuntime().getClass().getName()+".exec");//运行脚本命令
    }

    public static void addSecurityRiskMethod(Class clazz, String methodName )
    {
        QLExpressRunStrategy.securityRiskMethods.add(clazz.getName()+"."+methodName);
    }

    public static void assertBlackMethod(Method m) throws QLSecurityRiskException {

        if(forbiddenInvokeSecurityRiskMethods && m!=null){
            if(securityRiskMethods.contains(m.getDeclaringClass().getName()+"."+m.getName())) {
                throw new QLSecurityRiskException("使用QLExpress调用了不安全的系统方法:" + m.toString());
            }
        }
    }

}
