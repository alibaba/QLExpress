package com.alibaba.qlexpress4.runtime.data.implicit;


/**
 * @Author TaoKan
 * @Date 2022/12/25 上午8:15
 */
public class QLImplicitBase {
    protected final boolean needImplicitTrans;
    protected final QLImplicitVars vars;

    public QLImplicitBase(boolean needImplicitTrans, QLImplicitVars vars){
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
