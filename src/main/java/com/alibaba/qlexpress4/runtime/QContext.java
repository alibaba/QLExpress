package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.scope.QScope;

/**
 * Author: DQinYuan
 */
public interface QContext extends QScope, QRuntime {

    QScope getQScope();

}
