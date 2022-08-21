package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.cache.*;
import com.alibaba.qlexpress4.runtime.scope.QScope;

import java.util.Map;

/**
 * Author: DQinYuan
 */
public interface QRuntime {

    /**
     * define a symbol in global scope
     * for example, `Number a = 10`
     *              define("a", Number.class, new Value(10))
     * @param varName symbol name
     * @param varClz symbol clz, declare clz, not real clz
     */
    LeftValue defineSymbol(String varName, Class<?> varClz);

    /**
     * get script start time
     * @return start time
     */
    long scriptStartTimeStamp();

    Map<String, Object> attachment();
    /**
     * get instance cache from qvm
     * @return ICache
     */
    QLCaches getQLCaches();
}
