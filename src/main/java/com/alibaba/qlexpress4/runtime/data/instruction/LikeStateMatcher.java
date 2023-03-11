package com.alibaba.qlexpress4.runtime.data.instruction;

/**
 * @Author TaoKan
 * @Date 2023/1/30 下午9:13
 */
public class LikeStateMatcher {

    private LikeState current;

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
        return stateStatus;
    }
}
