package com.alibaba.qlexpress4.runtime.data.implicit;

/**
 * Author: TaoKan
 */
public class QLCandidateMethodAttr {
    private Class<?>[] paramClass;
    private int level;

    public QLCandidateMethodAttr(Class<?>[] paramClass, int level){
        this.paramClass = paramClass;
        this.level = level;
    }

    public Class<?>[] getParamClass() {
        return paramClass;
    }

    public void setParamClass(Class<?>[] paramClass) {
        this.paramClass = paramClass;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
