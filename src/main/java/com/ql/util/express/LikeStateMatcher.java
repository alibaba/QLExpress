package com.ql.util.express;

/**
 * @Author TaoKan
 * @Date 2023/1/30 下午9:13
 */
public class LikeStateMatcher {
    private LikeState current;
    private char lastWord = 0;

    public LikeStateMatcher(LikeState likeState){
        this.current = likeState;
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

    public LikeState getCurrent() {
        return current;
    }
}
