package com.alibaba.qlexpress4.runtime.context;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.exception.PureErrReporter;
import com.alibaba.qlexpress4.runtime.ReflectLoader;
import com.alibaba.qlexpress4.runtime.Value;

import java.util.Map;

public class ObjectFieldExpressContext implements ExpressContext {

    private final Object object;

    private final Express4Runner express4Runner;

    public ObjectFieldExpressContext(Object object, Express4Runner express4Runner) {
        this.object = object;
        this.express4Runner = express4Runner;
    }

    @Override
    public Value get(Map<String, Object> attachments, String variableName) {
        return express4Runner.loadField(object, variableName);
    }
}
