package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.scope.QScope;

/**
 * Author: DQinYuan
 */
public interface QContext extends QScope, QRuntime {

    QScope getCurrentScope();

    /**
     * converter scope relative jump relation to absolute jump position
     * @param relativeJump
     * @return absolute jump position
     */
    int absoluteJump(int relativeJump);

    Integer toHandlerScope(Object catchObj, QScope until);

    void closeScope();
}
