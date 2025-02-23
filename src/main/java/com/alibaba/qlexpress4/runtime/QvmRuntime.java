package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.trace.QTraces;

import java.util.Map;

/**
 * root runtime with external variable and function
 * Author: DQinYuan
 */
public class QvmRuntime implements QRuntime {

    private final QTraces traces;

    private final Map<String, Object> attachments;

    private final ReflectLoader reflectLoader;

    private final long startTime;

    public QvmRuntime(QTraces traces, Map<String, Object> attachments,
                      ReflectLoader reflectLoader, long startTime) {
        this.traces = traces;
        this.attachments = attachments;
        this.reflectLoader = reflectLoader;
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

    public ReflectLoader getReflectLoader() {
        return reflectLoader;
    }

    @Override
    public QTraces getTraces() {
        return traces;
    }
}
