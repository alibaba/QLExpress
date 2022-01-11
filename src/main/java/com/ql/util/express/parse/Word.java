package com.ql.util.express.parse;

public class Word {
    public final String word;
    public final int line;
    public final int col;
    public int index;

    public Word(String word, int line, int col) {
        this.word = word;
        this.line = line;
        this.col = col;
    }

    @Override
    public String toString() {
        return this.word;// + "[" + this.line + "," + this.col + "]";
    }
}
