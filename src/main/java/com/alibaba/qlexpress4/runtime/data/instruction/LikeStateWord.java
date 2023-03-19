package com.alibaba.qlexpress4.runtime.data.instruction;

/**
 * @Author TaoKan
 * @Date 2023/1/25 上午11:20
 */
public class LikeStateWord {
    private final String charWords;
    private final int likeWordPatternEnum;

    public LikeStateWord(final String charWords, final int likeWordPatternEnum){
        this.charWords = charWords;
        this.likeWordPatternEnum = likeWordPatternEnum;
    }

    public String getCharWords() {
        return charWords;
    }

    public int getLikeWordPatternEnum() {
        return likeWordPatternEnum;
    }

}
