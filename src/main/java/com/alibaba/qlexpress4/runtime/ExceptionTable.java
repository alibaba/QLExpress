package com.alibaba.qlexpress4.runtime;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class ExceptionTable {

    public static final ExceptionTable EMPTY = new ExceptionTable(Collections.emptyList(), null);

    private final List<Map.Entry<Class<?>, Integer>> handlerPosMap;

    /**
     * nullable
     */
    private final Integer finalPos;

    public ExceptionTable(List<Map.Entry<Class<?>, Integer>> handlerPosMap, Integer finalPos) {
        this.handlerPosMap = handlerPosMap;
        this.finalPos = finalPos;
    }

    public Integer getRelativePos(Object throwObj) {
        for (Map.Entry<Class<?>, Integer> classHandlerMap : handlerPosMap) {
            if (throwObj == null) {
                return classHandlerMap.getValue();
            } else if (classHandlerMap.getKey().isAssignableFrom(throwObj.getClass())) {
                return classHandlerMap.getValue();
            }
        }
        return null;
    }

    public Integer getFinalPos() {
        return finalPos;
    }
}
