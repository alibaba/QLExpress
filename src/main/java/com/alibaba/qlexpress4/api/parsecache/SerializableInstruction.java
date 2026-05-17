package com.alibaba.qlexpress4.api.parsecache;

import java.util.Map;

public class SerializableInstruction {
    private String opcode;
    
    private SerializableSource source;
    
    private Map<String, Object> operands;
    
    public SerializableInstruction() {
    }
    
    public String getOpcode() {
        return opcode;
    }
    
    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }
    
    public SerializableSource getSource() {
        return source;
    }
    
    public void setSource(SerializableSource source) {
        this.source = source;
    }
    
    public Map<String, Object> getOperands() {
        return operands;
    }
    
    public void setOperands(Map<String, Object> operands) {
        this.operands = operands;
    }
}
