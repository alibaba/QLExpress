package com.alibaba.qlexpress4.pf4j;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

/**
 * Test plugin implementation class
 */
public class TestPluginImpl extends Plugin {
    
    public TestPluginImpl(PluginWrapper wrapper) {
        super(wrapper);
    }
    
    @Override
    public void start() {
        System.out.println("TestPlugin started");
    }
    
    @Override
    public void stop() {
        System.out.println("TestPlugin stopped");
    }
}