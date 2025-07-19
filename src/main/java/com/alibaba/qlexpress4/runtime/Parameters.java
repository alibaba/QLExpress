package com.alibaba.qlexpress4.runtime;

/**
 * Author: DQinYuan
 */
public interface Parameters {
    
    default Object getValue(int i) {
        Value boxedValue = get(i);
        return boxedValue == null ? null : boxedValue.get();
    }
    
    /**
     * get parameters in i position
     *
     * @param i index
     * @return value in index, null if exceed parameters' length
     */
    Value get(int i);
    
    /**
     * parameters size
     * @return size
     */
    int size();
}
