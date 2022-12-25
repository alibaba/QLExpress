package com.alibaba.qlexpress4.runtime.data.implicit;

import com.alibaba.qlexpress4.runtime.QLambdaInner;


/**
 * @Author TaoKan
 * @Date 2022/12/24 下午7:05
 */
public class QLImplicitLambda extends QLImplicitBase{
    private final QLambdaInner qLambdaInner;

    public QLImplicitLambda(QLambdaInner qLambdaInner, boolean needImplicitTrans, QLImplicitVars vars){
        super(needImplicitTrans, vars);
        this.qLambdaInner = qLambdaInner;
    }

    public QLambdaInner getQLambdaInner() {
        return qLambdaInner;
    }

}
