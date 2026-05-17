package com.alibaba.qlexpress4.api.parsecache;

import java.util.List;

public class SerializableParseCache {
    private int modelVersion;
    
    private String producerVersion;
    
    private String script;
    
    private String scriptHash;
    
    private SerializableLambdaDefinition main;
    
    private List<SerializableTracePoint> tracePoints;
    
    public SerializableParseCache() {
    }
    
    public int getModelVersion() {
        return modelVersion;
    }
    
    public void setModelVersion(int modelVersion) {
        this.modelVersion = modelVersion;
    }
    
    public String getProducerVersion() {
        return producerVersion;
    }
    
    public void setProducerVersion(String producerVersion) {
        this.producerVersion = producerVersion;
    }
    
    public String getScript() {
        return script;
    }
    
    public void setScript(String script) {
        this.script = script;
    }
    
    public String getScriptHash() {
        return scriptHash;
    }
    
    public void setScriptHash(String scriptHash) {
        this.scriptHash = scriptHash;
    }
    
    public SerializableLambdaDefinition getMain() {
        return main;
    }
    
    public void setMain(SerializableLambdaDefinition main) {
        this.main = main;
    }
    
    public List<SerializableTracePoint> getTracePoints() {
        return tracePoints;
    }
    
    public void setTracePoints(List<SerializableTracePoint> tracePoints) {
        this.tracePoints = tracePoints;
    }
}
