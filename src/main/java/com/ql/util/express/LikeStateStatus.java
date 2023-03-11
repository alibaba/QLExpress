package com.ql.util.express;

/**
 * @Author TaoKan
 * @Date 2023/1/26 上午9:52
 */
public class LikeStateStatus {
    private final boolean result;
    private final LikeStateStatusEnum status;
    private final int stayJumpNum;

    public LikeStateStatus(boolean result, LikeStateStatusEnum status, int stayJumpNum){
        this.result = result;
        this.status = status;
        this.stayJumpNum = stayJumpNum;
    }

    public int getStayJumpNum() {
        return stayJumpNum;
    }
    public boolean getResult() {
        return result;
    }

    public LikeStateStatusEnum getStatus() {
        return status;
    }

    public enum LikeStateStatusEnum{
        GOTO_NEXT, GOTO_PREV, STAY, CYCLE, BREAK;
    }
}
