package com.alibaba.qlexpress4.runtime.data.process;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午6:40
 */
public class CandidateMethodAttr {

    private Class<?>[] paramClass;
    private int level;

    public CandidateMethodAttr(Class<?>[] paramClass, int level){
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
