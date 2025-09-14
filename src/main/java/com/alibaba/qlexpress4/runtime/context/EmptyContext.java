package com.alibaba.qlexpress4.runtime.context;

import com.alibaba.qlexpress4.runtime.Value;

import java.util.Map;

public class EmptyContext implements ExpressContext {
    @Override
    public Value get(Map<String, Object> attachments, String variableName) {
        return Value.NULL_VALUE;
    }
}
