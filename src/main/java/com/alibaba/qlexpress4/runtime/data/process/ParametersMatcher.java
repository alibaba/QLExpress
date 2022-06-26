package com.alibaba.qlexpress4.runtime.data.process;

/**
 * @Author TaoKan
 * @Date 2022/6/25 下午6:08
 */
public class ParametersMatcher {
    public static final int DEFAULT_INDEX = -1;
    public static final int DEFAULT_LEVEL = Integer.MAX_VALUE;
    private int matchLevel;
    private int index;
    private Class<?>[] parametersClassType;

    public ParametersMatcher() {
        this.matchLevel = DEFAULT_LEVEL;
        this.index = DEFAULT_INDEX;
    }

    public int getMatchLevel() {
        return matchLevel;
    }

    public void setMatchLevel(int matchLevel) {
        this.matchLevel = matchLevel;
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
