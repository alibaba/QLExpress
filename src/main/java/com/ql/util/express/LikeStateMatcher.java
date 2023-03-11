package com.ql.util.express;

/**
 * @Author TaoKan
 * @Date 2023/1/30 下午9:13
 */
public class LikeStateMatcher {
    private LikeState current;
    private int index;

    public LikeStateMatcher(LikeState likeState){
        this.current = likeState;
    }

    protected LikeStateStatus findState(char word, boolean isEndWord, int remainWords){
        LikeStateStatus stateStatus = this.current.matchWords(word, isEndWord, remainWords);
        if(stateStatus.getStatus().equals(LikeStateStatus.LikeStateStatusEnum.BREAK)){
            return stateStatus;
        }
        if(stateStatus.getStatus().equals(LikeStateStatus.LikeStateStatusEnum.GOTO_NEXT)){
            this.current = this.current.next();
        }
        if(stateStatus.getStatus().equals(LikeStateStatus.LikeStateStatusEnum.GOTO_PREV)){
            this.current = this.current.prev();
        }
        return stateStatus;
    }

    public LikeState getCurrent() {
        return current;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
