package com.alibaba.qlexpress4.runtime.data.instruction;

/**
 * @Author TaoKan
 * @Date 2023/1/30 下午9:13
 */
public class LikeStateMatcher {

    private LikeState current;

    public LikeStateMatcher(LikeState likeState) {
        this.current = likeState;
    }

    public LikeState getCurrent(){
        return this.current;
    }

    public void goNext() {
        this.current = this.current.next();
    }
}
