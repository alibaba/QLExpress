package com.ql.util.express;

/**
 * @Author TaoKan
 * @Date 2023/1/25 上午11:20
 */
public class LikeStateWord {
    private final String charWords;
    private final LikeWordPatternEnum likeWordPatternEnum;

    public LikeStateWord(final String charWords, final LikeWordPatternEnum likeWordPatternEnum){
        this.charWords = charWords;
        this.likeWordPatternEnum = likeWordPatternEnum;
    }

    public String getCharWords() {
        return charWords;
    }

    public LikeWordPatternEnum getLikeWordPatternEnum() {
        return likeWordPatternEnum;
    }

}
