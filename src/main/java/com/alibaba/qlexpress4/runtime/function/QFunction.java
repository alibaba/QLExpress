package com.alibaba.qlexpress4.runtime.function;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * Author: DQinYuan
 */
public interface QFunction {

    /**
     * @param qRuntime
     * @param parameters
     * @return
     * @throws Throwable
     *        {@link com.alibaba.qlexpress4.exception.UserDefineException} for custom error message
     */
    Object call(QRuntime qRuntime, Parameters parameters) throws Throwable;

}
