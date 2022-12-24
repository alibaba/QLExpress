package com.alibaba.qlexpress4.runtime.data.implicit;

import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.QLambdaInner;

import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/12/24 下午7:05
 */
public class QLImplicitLambda {
    private final boolean needImplicitTrans;
    private final QLImplicitVars vars;
    private final QLambdaInner qLambdaInner;

    public QLImplicitLambda(QLambdaInner qLambdaInner, boolean needImplicitTrans, QLImplicitVars vars){
        this.qLambdaInner = qLambdaInner;
        this.needImplicitTrans = needImplicitTrans;
        this.vars = vars;
    }

    public boolean needImplicitTrans() {
        return needImplicitTrans;
    }

    public QLImplicitVars getVars() {
        return vars;
    }
}
