package com.ql.util.express;

/**
 * @Author TaoKan
 * @Date 2023/1/25 上午11:20
 */
public class LikeState {
    private LikeStateWord info;
    private LikeState prev;
    private LikeState next;


    public LikeStateStatus matchWords(char charWord, char lastWord, boolean isEndWord){
        if(this.info.equals(charWord)){
            return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.GOTO_NEXT);
        }else{
            if(this.info.equalsPercentSign()){
                if(this.next != null){
                    if(isEndWord){
                        return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.DEST_STAY);
                    }else {
                        return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.GOTO_NEXT);
                    }
                }else {
                    return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.STAY);
                }
            }
            if(prev == null){
                return new LikeStateStatus(false, LikeStateStatus.LikeStateStatusEnum.STAY);
            }
            if(prev.info.equalsPercentSign()){
                if(this.info.equals(lastWord) && this.next != null){
                    return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.DEST_STAY);
                }else{
                    return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.STAY);
                }
            }
        }
        return new LikeStateStatus(false, LikeStateStatus.LikeStateStatusEnum.STAY);
    }

    public boolean getWordPercentSign(){
        return this.info.equalsPercentSign();
    }

    public LikeState next(){
        return this.next;
    }

    public void setNext(LikeState next){
        this.next = next;
    }

    public LikeState prev(){
        return this.prev;
    }

    public void setPrev(LikeState prev){
        this.prev = prev;
    }

    public void setInfo(LikeStateWord info) {
        this.info = info;
    }
}
