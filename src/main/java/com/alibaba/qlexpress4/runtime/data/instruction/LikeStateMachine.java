package com.alibaba.qlexpress4.runtime.data.instruction;

/**
 * @Author TaoKan
 * @Date 2023/1/25 上午11:26
 */
public class LikeStateMachine{
    private LikeState start;
    private LikeState current;
    private int size;
    private LikeStateMatchType likeStateMatchType;
    private String resultPattern;

    private LikeStateMachine(){
    }

    public static LikeStateMachineBuilder builder(){
        return new LikeStateMachineBuilder();
    }

    private void addLink(LikeState likeState){
        if(this.size == 0){
            this.start = likeState;
            this.current = likeState;
        }else {
            LikeState curr = this.current;
            likeState.setPrev(curr);
            curr.setNext(likeState);
            this.current = likeState;
        }
        size++;
    }

    private LikeStateMachine execute(String pattern){
        final int patternLen = pattern.length();
        LikeStateMachine likeStateMachine = new LikeStateMachine();
        StringBuilder stringBuilder = new StringBuilder();
        boolean endOfPercentSign = false, isRight = false, noSign = true;
        int index = 0, percentCount = 0;
        for(int i = 0; i < patternLen; i++){
            char word = pattern.charAt(i);
            if(i < patternLen - 1 && pattern.charAt(i) != '%' && pattern.charAt(i+1) == '%'){
                isRight = true;
            }
            if(word == '%'){
                if(i == patternLen - 1){
                    endOfPercentSign = true;
                }
                if(stringBuilder.toString().length() == 0){
                    index++;
                    percentCount++;
                }else {
                    createLinkNode(likeStateMachine,stringBuilder.toString(),
                            enhanceNormalWordsLikeWordPatternEnum(likeStateMachine,percentCount,index,isRight));
                    index+=2;
                    isRight = false;
                    percentCount++;
                    stringBuilder = new StringBuilder();
                }
                noSign = false;
            }else {
                stringBuilder.append(word);
            }
        }
        if(!endOfPercentSign){
            index++;
            if(percentCount == 0){
                createLinkNode(likeStateMachine, stringBuilder.toString(), LikeWordPatternEnum.NONE);
            }else {
                createLinkNode(likeStateMachine, stringBuilder.toString(),
                        enhanceNormalWordsLikeWordPatternEnum(likeStateMachine,percentCount,index, isRight));
            }
        }else {
            if(likeStateMachine.size == 0){
                createLinkNode(likeStateMachine, stringBuilder.toString(), LikeWordPatternEnum.NONE);
            }
        }
        likeStateMachine.current = likeStateMachine.start;
        if(noSign){
            likeStateMachine.likeStateMatchType = LikeStateMatchType.EQUALS;
            likeStateMachine.resultPattern = pattern;
        }
        if(likeStateMachine.likeStateMatchType == null){
            likeStateMachine.likeStateMatchType = LikeStateMatchType.COMPLEX;
        }
        return likeStateMachine;
    }


    private void setMatchType(LikeStateMachine likeStateMachine,LikeStateMatchType likeStateMatchType){
        if(likeStateMachine.likeStateMatchType == null){
            likeStateMachine.likeStateMatchType = likeStateMatchType;
        }else {
            likeStateMachine.likeStateMatchType = LikeStateMatchType.COMPLEX;
        }
    }

    private LikeWordPatternEnum enhanceNormalWordsLikeWordPatternEnum(LikeStateMachine likeStateMachine, int percentCount, int index, boolean isRight){
        if(isRight){
            if(percentCount == 0){
                setMatchType(likeStateMachine,LikeStateMatchType.START_WITH);
                return LikeWordPatternEnum.RIGHT_PERCENT_SIGN;
            }
            setMatchType(likeStateMachine,LikeStateMatchType.CONTAINS);
            return LikeWordPatternEnum.SURROUND_PERCENT_SIGN;
        }
        setMatchType(likeStateMachine,LikeStateMatchType.END_WITH);
        return LikeWordPatternEnum.LEFT_PERCENT_SIGN;
    }

    private void createLinkNode(LikeStateMachine likeStateMachine, String charWords, LikeWordPatternEnum likeWordPatternEnum){
        LikeStateWord likeStatementWord = new LikeStateWord(charWords, likeWordPatternEnum);
        LikeState likeState = new LikeState();
        likeState.setInfo(likeStatementWord);
        likeStateMachine.addLink(likeState);
        likeStateMachine.resultPattern = charWords;
    }

    private boolean matchEquals(String desc){
        return desc.equals(this.resultPattern);
    }
    private boolean matchContains(String desc){
        return desc.contains(this.resultPattern);
    }
    private boolean matchStartsWith(String desc){
        return desc.startsWith(this.resultPattern);
    }
    private boolean matchEndsWith(String desc){
        return desc.endsWith(this.resultPattern);
    }

    private boolean matchComplex(String dest){
        final int destLen = dest.length();
        LikeStateMatcher likeStateMatcher = new LikeStateMatcher(this.start);
        int stayJumpNum = 0;
        for(int i = 0; i < destLen; i++){
            if(stayJumpNum > 0){
                stayJumpNum --;
                continue;
            }
            char word = dest.charAt(i);
            LikeStateStatus likeStateStatus = likeStateMatcher.findState(word, i == destLen - 1, destLen - i);
            if(!likeStateStatus.getResult()){
                //中断
                return false;
            }else if(likeStateStatus.getStatus().equals(LikeStateStatus.LikeStateStatusEnum.BREAK)){
                //中断
                return true;
            }else if(likeStateStatus.getStatus().equals(LikeStateStatus.LikeStateStatusEnum.GOTO_NEXT)){
                stayJumpNum = likeStateStatus.getStayJumpNum();
            }
        }
        return true;
    }

    public boolean match(String dest){
        if(this.likeStateMatchType.equals(LikeStateMatchType.EQUALS)){
            return matchEquals(dest);
        }else if(this.likeStateMatchType.equals(LikeStateMatchType.CONTAINS)){
            return matchContains(dest);
        }else if(this.likeStateMatchType.equals(LikeStateMatchType.START_WITH)){
            return matchStartsWith(dest);
        }else if(this.likeStateMatchType.equals(LikeStateMatchType.END_WITH)){
            return matchEndsWith(dest);
        }else {
            return matchComplex(dest);
        }
    }

    public static class LikeStateMachineBuilder {
        private String pattern;

        public LikeStateMachineBuilder loadPattern(String pattern){
            this.pattern = pattern;
            return this;
        }

        public LikeStateMachine build(){
            return new LikeStateMachine().execute(this.pattern);
        }
    }

    private enum LikeStateMatchType{
        START_WITH,END_WITH,CONTAINS,EQUALS,COMPLEX
    }
}
