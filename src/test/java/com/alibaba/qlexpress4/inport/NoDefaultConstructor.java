package com.alibaba.qlexpress4.inport;

/**
 * A test helper class that intentionally has NO no-arg constructor,
 * used to verify that NewFilledInstanceInstruction reports a proper
 * error instead of NullPointerException.
 */
public class NoDefaultConstructor {
    
    private final String name;
    
    public NoDefaultConstructor(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
