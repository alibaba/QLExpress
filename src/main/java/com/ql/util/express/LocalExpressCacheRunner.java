package com.ql.util.express;

import java.util.HashMap;
import java.util.Map;

/**
 * 作为表达式
 *
 * @author tianqiao
 */
public class LocalExpressCacheRunner extends ExpressRemoteCacheRunner {

    private static Map<String, Object> expressMap = new HashMap<>();

    private ExpressRunner expressRunner;

    public LocalExpressCacheRunner(ExpressRunner expressRunner) {
        this.expressRunner = expressRunner;
    }

    @Override
    public final Object getCache(String key) {
        return expressMap.get(key);
    }

    @Override
    public final void putCache(String key, Object object) {
        expressMap.put(key, object);
    }

    @Override
    public final ExpressRunner getExpressRunner() {
        return this.expressRunner;
    }
}
