package com.alibaba.qlexpress4.runtime;

/**
 * Author: DQinYuan
 */
public class QFunctionInner implements QFunction {

    private final QLambda qLambda;

    public QFunctionInner(QLambda qLambda) {
        this.qLambda = qLambda;
    }

    @Override
    public Object call(QRuntime qRuntime, Parameters parameters) throws Exception {
        Object[] paramsArr = new Object[parameters.size()];
        for (int i = 0; i < paramsArr.length; i++) {
            paramsArr[i] = parameters.get(i);
        }
        return qLambda.call(paramsArr).getResult().get();
    }
}
