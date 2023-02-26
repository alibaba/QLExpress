package com.ql.util.express.instruction.op;

import com.ql.util.express.LikeStateMachine;
import com.ql.util.express.Operator;

public class OperatorLike extends Operator {
    public static final char PERCENT_SIGN = '%';

    public OperatorLike(String name) {
        this.name = name;
    }

    public OperatorLike(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) throws Exception {
//        LikeStateMachine machine = LikeStateMachine.builder().loadPattern(op2.toString()).build();
//        return machine.match(op1.toString());
        return likeMatch(op1.toString(),op2.toString());
    }

    public boolean likeMatch(String dest, String pattern){
        int i = 0, j = 0;
        int patternLen = pattern.length();
        int destLen = dest.length();
        char prePatternChar = 0;
        for(;i < destLen;){
            if(j == patternLen){
                break;
            }
            char patternWord = pattern.charAt(j);
            char descWord = dest.charAt(i);
            if(patternWord == descWord){
                j++;
                i++;
                prePatternChar = patternWord;
                continue;
            }
            if(patternWord == PERCENT_SIGN){
                if(j != patternLen - 1){
                    j++;
                }else {
                    i++;
                }
                prePatternChar = patternWord;
                continue;
            }
            if(prePatternChar == PERCENT_SIGN){
                i++;
                continue;
            }
            return false;
        }
        if(j != patternLen){
            if(notEndOfCharWord(pattern,PERCENT_SIGN,j)){
                return false;
            }else {
                return true;
            }
        }
        if(i != destLen){
            if(prePatternChar == dest.charAt(destLen-1)){
                return true;
            }
            if(notEndOfCharWord(dest,prePatternChar,i)){
                return false;
            }else {
                return true;
            }
        }
        return true;
    }

    protected boolean notEndOfCharWord(String charWord, char compareWord, int index){
        for(int i = index; i < charWord.length(); i++){
            char thisWord = charWord.charAt(i);
            if(thisWord != compareWord){
                return true;
            }
        }
        return false;
    }

}
