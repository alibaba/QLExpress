package com.alibaba.qlexpress4.runtime.data.implicit;

/**
 * @Author TaoKan
 * @Date 2022/6/29 下午4:24
 */
public class QLImplicitMatcher {
    private final Boolean needImplicitTrans;
    private final Integer matchIndex;

    public QLImplicitMatcher(Boolean needImplicitTrans, Integer matchIndex){
        this.needImplicitTrans = needImplicitTrans;
        this.matchIndex = matchIndex;
    }

    public Boolean needImplicitTrans() {
        return needImplicitTrans;
    }

    public Integer getMatchIndex() {
        return matchIndex;
    }


}
