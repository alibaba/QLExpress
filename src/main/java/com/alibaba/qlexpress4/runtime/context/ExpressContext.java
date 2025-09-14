package com.alibaba.qlexpress4.runtime.context;

import com.alibaba.qlexpress4.runtime.Value;

import java.util.Map;

/**
 * Author: DQinYuan
 */
public interface ExpressContext {
    
    ExpressContext EMPTY_CONTEXT = new EmptyContext();
    
    Value get(Map<String, Object> attachments, String variableName);
    
}
