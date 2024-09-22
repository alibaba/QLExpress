package com.alibaba.qlexpress4.runtime.context;

import com.alibaba.qlexpress4.exception.PureErrReporter;
import com.alibaba.qlexpress4.runtime.ReflectLoader;
import com.alibaba.qlexpress4.runtime.Value;

import java.util.Map;

public class ObjectFieldExpressContext implements ExpressContext {

    private final Object object;

    private final ReflectLoader reflectLoader;

    public ObjectFieldExpressContext(Object object, ReflectLoader reflectLoader) {
        this.object = object;
        this.reflectLoader = reflectLoader;
    }

    @Override
    public Value get(Map<String, Object> attachments, String variableName) {
        return reflectLoader.loadField(object, variableName, true, PureErrReporter.INSTANCE);
    }
}
