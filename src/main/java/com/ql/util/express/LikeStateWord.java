package com.ql.util.express;

/**
 * @Author TaoKan
 * @Date 2023/1/25 上午11:20
 */
public class LikeStateWord {
    private final int index;
    private final char charWord;

    public LikeStateWord(int index, char charWord){
        this.index = index;
        this.charWord = charWord;
    }

    public int getIndex() {
        return index;
    }

    public char getCharWord() {
        return charWord;
    }

    public boolean equals(char word){
        return charWord == word;
    }

    public boolean equalsPercentSign(){
        return charWord == '%';
    }

}
