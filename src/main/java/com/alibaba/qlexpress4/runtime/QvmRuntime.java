package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.runtime.data.AssignableDataValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;
import com.alibaba.qlexpress4.runtime.scope.QScope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * root runtime with external variable and function
 * Author: DQinYuan
 */
public class QvmRuntime implements QRuntime {

    private final Map<String, Object> attachments;

    private final QLCaches qlCaches;

    private final long startTime;

    public QvmRuntime(Map<String, Object> attachments, QLCaches qlCaches, long startTime) {
        this.attachments = attachments;
        this.qlCaches = qlCaches;
        this.startTime = startTime;
    }

    @Override
    public long scriptStartTimeStamp() {
        return startTime;
    }

    @Override
    public Map<String, Object> attachment() {
        return attachments;
    }

    @Override
    public QLCaches getQLCaches() {
        return qlCaches;
    }
}
