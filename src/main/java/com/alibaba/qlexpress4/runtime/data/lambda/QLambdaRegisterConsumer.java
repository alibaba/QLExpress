package com.alibaba.qlexpress4.runtime.data.lambda;

import com.alibaba.qlexpress4.runtime.QResult;
import java.util.function.Consumer;

/**
 * @Author TaoKan
 * @Date 2022/8/6 下午4:47
 */
public class QLambdaRegisterConsumer<T> extends QLambdaRegister<Consumer> {
    private final Consumer<T> consumer;

    public QLambdaRegisterConsumer(Consumer<T> consumer){
        this.consumer = consumer;
    }

    @Override
    public QResult call(Object... params) throws Exception {
        consumer.accept((T) params[0]);
        return new QResult(null,QResult.ResultType.RETURN);
    }
}
