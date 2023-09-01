package com.alibaba.qlexpress4.runtime;

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

    ReflectLoader getReflectLoader();
}
