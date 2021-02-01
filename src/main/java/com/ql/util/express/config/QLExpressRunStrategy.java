package com.ql.util.express.config;

import com.ql.util.express.exception.QLSecurityRiskException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * ExpressRunner Set the globally effective configuration, directly use the static method to control
 */
public class QLExpressRunStrategy {


    /**
     * Null pointer prevention
     */

    private static boolean avoidNullPointer = false;

    /**
     * When comparing the size of an empty object, it returns false, for example, 1> null and null> 1 both return false
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



    private static boolean forbiddenInvokeSecurityRiskMethods = false;

    public static boolean isForbiddenInvokeSecurityRiskMethods() {
        return forbiddenInvokeSecurityRiskMethods;
    }

    public static void setForbiddenInvokeSecurityRiskMethods(boolean forbiddenInvokeSecurityRiskMethods) {
        QLExpressRunStrategy.forbiddenInvokeSecurityRiskMethods = forbiddenInvokeSecurityRiskMethods;
    }

    private static List<String>securityRiskMethods = new ArrayList<String>();

    static{
        securityRiskMethods.add(System.class.getName()+"."+"exit");
        securityRiskMethods.add(Runtime.getRuntime().getClass().getName()+".exec");
    }

    public static void addSecurityRiskMethod(Class clazz, String methodName )
    {
        QLExpressRunStrategy.securityRiskMethods.add(clazz.getName()+"."+methodName);
    }

    public static void assertBlackMethod(Method m) throws QLSecurityRiskException {

        if(forbiddenInvokeSecurityRiskMethods && m!=null){
            if(securityRiskMethods.contains(m.getDeclaringClass().getName()+"."+m.getName())) {
                throw new QLSecurityRiskException("An unsafe system method was called using QLExpress: " + m.toString());
            }
        }
    }

}
