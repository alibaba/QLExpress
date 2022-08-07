package com.alibaba.qlexpress4.runtime.data.implicit;

/**
 * @Author TaoKan
 * @Date 2022/8/6 下午1:56
 */
public class QLImplicitVars {
    private final boolean needVarsConvert;
    private final int varsIndex;

    public QLImplicitVars(boolean needVarsConvert, int varsIndex){
        this.needVarsConvert = needVarsConvert;
        this.varsIndex = varsIndex;
    }

    public boolean needVarsConvert() {
        return needVarsConvert;
    }

    public int getVarsIndex() {
        return varsIndex;
    }

}
