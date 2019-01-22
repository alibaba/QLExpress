package com.ql.util.express.config;

/**
 * ExpressRunner设置全局生效的配置，直接使用静态方法控制
 */
public class QLExpressRunStrategy {
    
    private static boolean avoidNullPointer = false;
    
    public static boolean isAvoidNullPointer() {
        return avoidNullPointer;
    }
    
    public static void setAvoidNullPointer(boolean avoidNullPointer) {
        QLExpressRunStrategy.avoidNullPointer = avoidNullPointer;
    }
}
