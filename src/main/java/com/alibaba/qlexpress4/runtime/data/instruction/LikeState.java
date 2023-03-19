package com.alibaba.qlexpress4.runtime.data.instruction;

/**
 * @Author TaoKan
 * @Date 2023/1/25 上午11:20
 */
public class LikeState {
    private LikeStateWord info;
    private LikeState prev;
    private LikeState next;
    private int index;

    public boolean isCurrentLikePatternEquals(int likeWordPatternEnum) {
        return this.info.getLikeWordPatternEnum() == likeWordPatternEnum;
    }

    public boolean isNextLikePatternEquals(int likeWordPatternEnum) {
        return this.next().info.getLikeWordPatternEnum() == likeWordPatternEnum;
    }

    public String getWords(){
        return this.info.getCharWords();
    }

    public LikeState next() {
        return this.next;
    }

    public void setNext(LikeState next) {
        this.next = next;
    }

    public LikeState prev() {
        return this.prev;
    }

    public void setPrev(LikeState prev) {
        this.prev = prev;
    }

    public void setInfo(LikeStateWord info) {
        this.info = info;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
