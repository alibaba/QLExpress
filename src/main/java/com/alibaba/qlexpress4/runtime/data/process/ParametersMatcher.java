package com.alibaba.qlexpress4.runtime.data.process;

import com.alibaba.qlexpress4.utils.BasicUtil;

/**
 * @Author TaoKan
 * @Date 2022/6/25 下午6:08
 */
public class ParametersMatcher {
    private int matchWeight;
    private int index;
    private Class<?>[] parametersClassType;

    public ParametersMatcher() {
        this.matchWeight = BasicUtil.DEFAULT_WEIGHT;
        this.index = BasicUtil.DEFAULT_MATCH_INDEX;
    }

    public int getMatchWeight() {
        return matchWeight;
    }

    public void setMatchWeight(int matchWeight) {
        this.matchWeight = matchWeight;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Class<?>[] getParametersClassType() {
        return parametersClassType;
    }

    public void setParametersClassType(Class<?>[] parametersClassType) {
        this.parametersClassType = parametersClassType;
    }

}
