package com.alibaba.qlexpress4.runtime.data.implicit;

import com.alibaba.qlexpress4.utils.BasicUtil;

/**
 * Author: TaoKan
 */
public class QLParametersMatcher {
    private int matchWeight;
    private int index;
    private Class<?>[] parametersClassType;
    private QLImplicitVars qlImplicitVars;

    public QLParametersMatcher() {
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

    public QLImplicitVars getQlImplicitVars() {
        return qlImplicitVars;
    }

    public void setQlImplicitVars(QLImplicitVars qlImplicitVars) {
        this.qlImplicitVars = qlImplicitVars;
    }

}
