
package com.ql.util.express.parse;

import com.ql.util.express.exception.QLCompileException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**
 * Grammar analysis class
 * 1. Word decomposition
 * @author xuannan
 *
 */

public class WordSplit
{
   /**
    * Text analysis function, "." is handled as an operation symbol
    * @param str String
    * @throws Exception
    * @return String[]
    */
   public static Word[] parse(String[] splitWord,String str) throws Exception{
	    if (str == null){
	        return new Word[0];
	     }
	     char c;
	     int line =1;
	     List<Word> list = new ArrayList<Word>();
	     int i= 0;
	     int point = 0;
	   // The offset of the first character of the current line from the beginning of the script
	   int currentLineOffset = 0;
	     while(i<str.length()){
	        c = str.charAt(i);
	       if (c=='"' || c=='\''){//String processing
	     	int index = str.indexOf(c,i + 1);
	     	//Dealing with "problems in strings
	         while(index >0 && str.charAt(index - 1) =='\\'){
	         	index = str.indexOf(c,index + 1);
	         }
	         if (index < 0)
	         	throw new QLCompileException("String is not closed");
	         String tempDealStr = str.substring(i,index + 1);
	         //Handle the situation of \\,\"
	         String tmpResult = "";
	         int tmpPoint = tempDealStr.indexOf("\\");        
	         while(tmpPoint >=0 ){
	         	tmpResult = tmpResult + tempDealStr.substring(0,tmpPoint);
	         	if(tmpPoint == tempDealStr.length() -1){
	         		throw new QLCompileException("In the string" + "\\error:" + tempDealStr);
	         	}
	         	tmpResult = tmpResult + tempDealStr.substring(tmpPoint + 1 ,tmpPoint + 2);
	         	tempDealStr = tempDealStr.substring(tmpPoint + 2);
	         	tmpPoint = tempDealStr.indexOf("\\");  
	         }
	         tmpResult = tmpResult + tempDealStr;
	         list.add(new Word(tmpResult,line,i - currentLineOffset + 1));

	         if (point < i ){
	             list.add(new Word(str.substring(point,i),line,point - currentLineOffset + 1));
	         }
	         i = index + 1;
	         point = i;
	       }else if(c=='.' && point < i && isNumber(str.substring(point,i))){
	    	   i = i + 1; //Special handling of decimal point
	       }else if(c == ' ' ||c =='\r'|| c =='\n'||c=='\t'||c=='\u000C'){
	    	    if (point < i ){
		             list.add(new Word(str.substring(point,i),line,point - currentLineOffset + 1));
		        }
		        if(c =='\n'){
		        	line = line + 1;
					currentLineOffset = i + 1;
		        } 
		        i = i + 1;
		        point = i;
		   }else{
	    	   boolean isFind = false;
	    	   for(String s:splitWord){
	    		   int length = s.length();
	    		   if(i + length <= str.length() && str.substring(i, i+length).equals(s)){
	    			   if (point < i ){
	    		             list.add(new Word(str.substring(point,i),line,point - currentLineOffset + 1));
	    		       }
                       list.add(new Word(str.substring(i, i+length),line,i - currentLineOffset + 1));
	    			   i = i + length;
	    			   point = i;
	    			   isFind = true;
	    			   break;
	    		   } 
	    	   }
	    	   if(isFind == false){
	    		   i = i+1;
	    	   }
	       }
	     }
		if (point < i) {
			list.add(new Word(str.substring(point, i), line, point - currentLineOffset + 1));
		}

		Word result[] = new Word[list.size()];
		list.toArray(result);
		return result;
	   }

	public static String[] sortSplitWord(String[] splitWord) {
		Arrays.sort(splitWord, new Comparator<String>() {
			public int compare(String o1, String o2) {
				if (o1.length() == o2.length()) {
					return 0;
				} else if (o1.length() > o2.length()) {
					return -1;
				} else {
					return 1;
				}

			}
		});
		return splitWord;
	}

	protected static boolean isNumber(String str) {
		if (str == null || str.equals(""))
			return false;
		char c = str.charAt(0);
		if (c >= '0' && c <= '9') { // digital
			return true;
		} else {
			return false;
		}
	}  


   public static String getPrintInfo(Object[] list,String splitOp){
	  	StringBuffer buffer = new StringBuffer();
		for(int i=0;i<list.length;i++){
			if(i > 0){buffer.append(splitOp);}
			buffer.append("{" + list[i] +"}");
		}
		return buffer.toString();
	  }

}
