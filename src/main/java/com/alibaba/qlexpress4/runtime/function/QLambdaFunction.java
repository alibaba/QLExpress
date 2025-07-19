package com.alibaba.qlexpress4.runtime.function;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * Author: DQinYuan
 */
public class QLambdaFunction implements CustomFunction {
    
    private final QLambda qLambda;
    
    public QLambdaFunction(QLambda qLambda) {
        this.qLambda = qLambda;
    }
    
    @Override
    public Object call(QContext qContext, Parameters parameters)
        throws Throwable {
        Object[] paramsArr = new Object[parameters.size()];
        for (int i = 0; i < paramsArr.length; i++) {
            paramsArr[i] = parameters.get(i).get();
        }
        return qLambda.call(paramsArr).getResult().get();
    }
}
