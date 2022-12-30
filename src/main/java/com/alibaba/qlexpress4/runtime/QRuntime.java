package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.cache.*;
import com.alibaba.qlexpress4.runtime.scope.QScope;

import java.util.Map;

/**
 * Author: DQinYuan
 */
public interface QRuntime {

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
