package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.QLOptions;

/**
 * Author: DQinYuan
 */
public interface QLambdaDefinition {

    QLambda toLambda(QRuntime qRuntime, QLOptions qlOptions,
                            boolean newEnv);

}
