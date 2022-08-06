package com.alibaba.qlexpress4.runtime.data.lambda;

import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import java.util.function.Predicate;

/**
 * @Author TaoKan
 * @Date 2022/8/6 下午4:47
 */
public class QLambdaRegisterPredicate<T> extends QLambdaRegister<Predicate> {
    private final Predicate<T> predicate;

    public QLambdaRegisterPredicate(Predicate<T> predicate){
        this.predicate = predicate;
    }

    @Override
    public QResult call(Object... params) throws Exception {
        boolean r = predicate.test((T) params[0]);
        return new QResult(new DataValue(r),QResult.ResultType.RETURN);
    }
}
