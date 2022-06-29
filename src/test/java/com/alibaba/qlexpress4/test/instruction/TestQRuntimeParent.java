package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.utils.CacheUtil;

/**
 * @Author TaoKan
 * @Date 2022/5/15 上午11:03
 */
public class TestQRuntimeParent implements QRuntime {
    private Value value;
    private Parameters parameters;

    public Value getValue() {
        return value;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public LeftValue getSymbol(String varName) {
        return null;
    }

    @Override
    public Value getSymbolValue(String varName) {
        return null;
    }

    @Override
    public LeftValue defineSymbol(String varName, Class<?> varClz) {
        return null;
    }

    @Override
    public void defineLocalSymbol(String varName, Class<?> varClz, Object value) {

    }



    @Override
    public void push(Value value) {
        this.value = value;
    }

    public void pushParameter(Parameters parameters){
        this.parameters = parameters;
    }

    @Override
    public Parameters pop(int number) {
        return this.parameters;
    }

    @Override
    public Value pop() {
        return this.value;
    }



    @Override
    public long scriptStartTimeStamp() {
        return 0;
    }

    @Override
    public QLCaches getQLCaches() {
        int size = 128;
        boolean enableUseCacheClear = false;
        QLCaches qlCaches = new QLCaches();
        qlCaches.setQlConstructorCache(CacheUtil.initConstructorCache(size,enableUseCacheClear));
        qlCaches.setQlFieldCache(CacheUtil.initFieldCache(size,enableUseCacheClear));
        qlCaches.setQlMethodCache(CacheUtil.initMethodCache(size,enableUseCacheClear));
        qlCaches.setQlMethodInvokeCache(CacheUtil.initMethodInvokeCache(size,enableUseCacheClear));
        qlCaches.setQlScriptCache(CacheUtil.initScriptCache(size,true));
        return qlCaches;
    }
}
