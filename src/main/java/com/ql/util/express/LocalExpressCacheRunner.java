package com.ql.util.express;

import java.util.HashMap;
import java.util.Map;

/**
 * 作为表达式
 *
 * @author tianqiao
 */
public class LocalExpressCacheRunner extends ExpressRemoteCacheRunner {
    private static final Map<String, Object> EXPRESS_MAP = new HashMap<>();

    private final ExpressRunner expressRunner;

    public LocalExpressCacheRunner(ExpressRunner expressRunner) {
        this.expressRunner = expressRunner;
    }

    @Override
    public final Object getCache(String key) {
        return EXPRESS_MAP.get(key);
    }

    @Override
    public final void putCache(String key, Object object) {
        EXPRESS_MAP.put(key, object);
    }

    @Override
    public final ExpressRunner getExpressRunner() {
        return this.expressRunner;
    }
}
