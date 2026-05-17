package com.alibaba.qlexpress4.api.parsecache;

import java.util.List;

public class SerializableLambdaDefinition {
    private String name;
    
    private List<SerializableInstruction> instructions;
    
    private List<SerializableParam> params;
    
    private int maxStackSize;
    
    public SerializableLambdaDefinition() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<SerializableInstruction> getInstructions() {
        return instructions;
    }
    
    public void setInstructions(List<SerializableInstruction> instructions) {
        this.instructions = instructions;
    }
    
    public List<SerializableParam> getParams() {
        return params;
    }
    
    public void setParams(List<SerializableParam> params) {
        this.params = params;
    }
    
    public int getMaxStackSize() {
        return maxStackSize;
    }
    
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }
}
