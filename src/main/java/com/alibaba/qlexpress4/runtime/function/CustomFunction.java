package com.alibaba.qlexpress4.runtime.function;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;

/**
 * Author: DQinYuan
 */
public interface CustomFunction {
    
    /**
     * @param qContext context of current script run
     * @param parameters parameters
     * @return result of function
     * @throws Throwable
     *        {@link com.alibaba.qlexpress4.exception.UserDefineException} for custom error message
     */
    Object call(QContext qContext, Parameters parameters)
        throws Throwable;
    
}
