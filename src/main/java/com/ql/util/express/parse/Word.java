package com.ql.util.express.parse;

public class Word {
    public final String word;
    public final int line;
    public final int col;
    public int index;

    public Word(String aWord, int aLine, int aCol) {
        this.word = aWord;
        this.line = aLine;
        this.col = aCol;
    }

    @Override
    public String toString() {
        return this.word;// + "[" + this.line + "," + this.col + "]";
    }
}
