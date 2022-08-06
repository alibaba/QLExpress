package com.alibaba.qlexpress4.runtime.data.lambda;

import com.alibaba.qlexpress4.runtime.QLFunctional;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;

/**
 * @Author TaoKan
 * @Date 2022/8/6 下午4:47
 */
public class QLambdaRegisterFunctional<T,R> extends QLambdaRegister<QLFunctional> {
    private final QLFunctional<T,R> qlFunctional;

    public QLambdaRegisterFunctional(QLFunctional<T,R> qlFunctional){
        this.qlFunctional = qlFunctional;
    }

    @Override
    public QResult call(Object... params) throws Exception {
        R result = qlFunctional.call((T)params[0]);
        return new QResult(new DataValue(result,result.getClass()), QResult.ResultType.RETURN);
    }
}
