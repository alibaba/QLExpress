package com.alibaba.qlexpress4.runtime.data.lambda;

import com.alibaba.qlexpress4.runtime.QResult;

/**
 * @Author TaoKan
 * @Date 2022/8/6 下午4:47
 */
public class QLambdaRegisterRunnable extends QLambdaRegister<Runnable> {
    private final Runnable runnable;

    public QLambdaRegisterRunnable(Runnable runnable){
        this.runnable = runnable;
    }

    @Override
    public QResult call(Object... params) throws Exception {
        runnable.run();
        return new QResult(null,QResult.ResultType.RETURN);
    }
}
