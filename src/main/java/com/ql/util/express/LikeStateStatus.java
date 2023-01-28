package com.ql.util.express;

/**
 * @Author TaoKan
 * @Date 2023/1/26 上午9:52
 */
public class LikeStateStatus {
    private final boolean result;
    private final LikeStateStatusEnum status;

    public LikeStateStatus(boolean result, LikeStateStatusEnum status){
        this.result = result;
        this.status = status;
    }

    public boolean getResult() {
        return result;
    }

    public LikeStateStatusEnum getStatus() {
        return status;
    }

    public enum LikeStateStatusEnum{
        GOTO_NEXT, GOTO_PREV, STAY, DEST_STAY;
    }
}
