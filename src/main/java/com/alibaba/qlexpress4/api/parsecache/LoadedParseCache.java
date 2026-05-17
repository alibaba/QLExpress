package com.alibaba.qlexpress4.api.parsecache;

import com.alibaba.qlexpress4.aparser.QCompileCache;

public class LoadedParseCache {
    private final QCompileCache compileCache;
    
    private final SerializableParseCache sourceCache;
    
    private final Object runnerIdentity;
    
    LoadedParseCache(QCompileCache compileCache, SerializableParseCache sourceCache, Object runnerIdentity) {
        this.compileCache = compileCache;
        this.sourceCache = sourceCache;
        this.runnerIdentity = runnerIdentity;
    }
    
    public int getModelVersion() {
        return sourceCache.getModelVersion();
    }
    
    public String getProducerVersion() {
        return sourceCache.getProducerVersion();
    }
    
    public String getScript() {
        return sourceCache.getScript();
    }
    
    public String getScriptHash() {
        return sourceCache.getScriptHash();
    }
    
    public boolean hasTracePoints() {
        return sourceCache.getTracePoints() != null;
    }
    
    public QCompileCache getCompileCache() {
        return compileCache;
    }
    
    public SerializableParseCache getSourceCache() {
        return sourceCache;
    }
    
    public boolean isBoundTo(Object runnerIdentity) {
        return this.runnerIdentity == runnerIdentity;
    }
}
