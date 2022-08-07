package com.alibaba.qlexpress4.cache;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午5:09
 */
public class QLCaches {
    private QLConstructorCache qlConstructorCache;
    private QLFieldCache qlFieldCache;
    private QLMethodCache qlMethodCache;
    private QLMethodInvokeCache qlMethodInvokeCache;
    private QLScriptCache qlScriptCache;

    public QLConstructorCache getQlConstructorCache() {
        return qlConstructorCache;
    }

    public void setQlConstructorCache(QLConstructorCache qlConstructorCache) {
        this.qlConstructorCache = qlConstructorCache;
    }

    public QLFieldCache getQlFieldCache() {
        return qlFieldCache;
    }

    public void setQlFieldCache(QLFieldCache qlFieldCache) {
        this.qlFieldCache = qlFieldCache;
    }

    public QLMethodCache getQlMethodCache() {
        return qlMethodCache;
    }

    public void setQlMethodCache(QLMethodCache qlMethodCache) {
        this.qlMethodCache = qlMethodCache;
    }

    public QLMethodInvokeCache getQlMethodInvokeCache() {
        return qlMethodInvokeCache;
    }

    public void setQlMethodInvokeCache(QLMethodInvokeCache qlMethodInvokeCache) {
        this.qlMethodInvokeCache = qlMethodInvokeCache;
    }

    public QLScriptCache getQlScriptCache() {
        return qlScriptCache;
    }

    public void setQlScriptCache(QLScriptCache qlScriptCache) {
        this.qlScriptCache = qlScriptCache;
    }

}
