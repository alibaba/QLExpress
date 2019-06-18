package com.ql.util.express.parse;


public class Word {
   public String word;
   public int line;
   public int col;
   public int index;
   public Word(String aWord,int aLine,int aCol){
	   this.word = aWord;
	   this.line = aLine;
	   this.col = aCol;
   }
  public String toString(){
	  return this.word;// + "[" + this.line + "," + this.col + "]";
  }
}
