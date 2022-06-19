package com.alibaba.qlexpress4.runtime;

/**
 * Author: DQinYuan
 */
public interface Parameters {

    /**
     * get parameters in i position
     * @param i index
     * @return value in index, null if exceed parameters' length
     */
    Value get(int i);

}
