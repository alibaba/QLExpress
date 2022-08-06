package com.alibaba.qlexpress4.runtime.data.lambda;

import com.alibaba.qlexpress4.runtime.QLFunctionalVarargs;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;

import java.lang.reflect.Array;

/**
 * @Author TaoKan
 * @Date 2022/8/6 下午4:47
 */
public class QLambdaRegisterFunctionalVarargs<T,R> extends QLambdaRegister<QLFunctionalVarargs> {
    private final QLFunctionalVarargs<T,R> qlFunctionalVarargs;
    private final Class<?> type;

    public QLambdaRegisterFunctionalVarargs(QLFunctionalVarargs<T,R> qlFunctionalVarargs, Class<?> type){
        this.qlFunctionalVarargs = qlFunctionalVarargs;
        this.type = type;
    }

    @Override
    public QResult call(Object... params) throws Exception {
        Object array = Array.newInstance(type, params.length);
        for(int i = 0; i < params.length; i++){
            Array.set(array, i, params[i]);
        }
        R result = qlFunctionalVarargs.call((T[])array);
        return new QResult(new DataValue(result,result.getClass()), QResult.ResultType.RETURN);
    }
}
