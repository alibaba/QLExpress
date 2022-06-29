package com.alibaba.qlexpress4.runtime.data.implicit;

/**
 * @Author TaoKan
 * @Date 2022/6/29 下午4:24
 */
public class QLImplicitMatcher {
    private final boolean needImplicitTrans;
    private final int matchIndex;

    public QLImplicitMatcher(boolean needImplicitTrans, int matchIndex){
        this.needImplicitTrans = needImplicitTrans;
        this.matchIndex = matchIndex;
    }

    public boolean needImplicitTrans() {
        return needImplicitTrans;
    }

    public int getMatchIndex() {
        return matchIndex;
    }
}
