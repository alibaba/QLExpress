package com.ql.util.express;

/**
 * @Author TaoKan
 * @Date 2023/1/25 上午11:26
 */
public class LikeStateMachine{
    private LikeState start;
    private LikeState current;
    private char lastWord = 0;
    private int size;

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
        int patternLen = pattern.length();
        LikeStateMachine likeStateMachine = new LikeStateMachine();
        for(int i = 0; i < patternLen; i++){
            LikeState likeState = new LikeState();
            LikeStateWord likeStatementWord = new LikeStateWord(i,pattern.charAt(i));
            likeState.setInfo(likeStatementWord);
            likeStateMachine.addLink(likeState);
        }
        likeStateMachine.current = likeStateMachine.start;
        return likeStateMachine;
    }

    public boolean match(String dest){
        int destLen = dest.length();
        for(int i = 0; i < destLen; i++){
            char word = dest.charAt(i);
            if(!findState(word, i == destLen - 1)){
                return false;
            }
        }
        return notEndOfCharWord(this.current);
    }

    protected boolean findState(char word, boolean isEndWord){
        if(this.current == null){
            return false;
        }
        LikeStateStatus stateStatus = this.current.matchWords(word, this.lastWord, isEndWord);
        if(!stateStatus.getResult()){
            return false;
        }
        if(stateStatus.getStatus().equals(LikeStateStatus.LikeStateStatusEnum.DEST_STAY)){
            this.current = this.current.next();
            return findState(word,isEndWord);
        }
        if(stateStatus.getStatus().equals(LikeStateStatus.LikeStateStatusEnum.GOTO_NEXT)){
            this.current = this.current.next();
        }
        if(stateStatus.getStatus().equals(LikeStateStatus.LikeStateStatusEnum.GOTO_PREV)){
            this.current = this.current.prev();
        }
        this.lastWord = word;
        return true;
    }

    protected boolean notEndOfCharWord(LikeState likeState){
        while (likeState != null){
            if(!likeState.getWordPercentSign()){
                return false;
            }
            likeState = likeState.next();
        }
        return true;
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

}
