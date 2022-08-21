package com.alibaba.qlexpress4.cache;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午5:09
 */
public class QLCaches {
    private final QLConstructorCache qlConstructorCache;
    private final QLFieldCache qlFieldCache;
    private final QLMethodCache qlMethodCache;
    private final QLMethodInvokeCache qlMethodInvokeCache;
    private final QLScriptCache qlScriptCache;

    public QLCaches(QLConstructorCache qlConstructorCache, QLFieldCache qlFieldCache, QLMethodCache qlMethodCache, QLMethodInvokeCache qlMethodInvokeCache, QLScriptCache qlScriptCache) {
        this.qlConstructorCache = qlConstructorCache;
        this.qlFieldCache = qlFieldCache;
        this.qlMethodCache = qlMethodCache;
        this.qlMethodInvokeCache = qlMethodInvokeCache;
        this.qlScriptCache = qlScriptCache;
    }

    public QLConstructorCache getQlConstructorCache() {
        return qlConstructorCache;
    }

    public QLFieldCache getQlFieldCache() {
        return qlFieldCache;
    }

    public QLMethodCache getQlMethodCache() {
        return qlMethodCache;
    }

    public QLMethodInvokeCache getQlMethodInvokeCache() {
        return qlMethodInvokeCache;
    }

    public QLScriptCache getQlScriptCache() {
        return qlScriptCache;
    }

}
