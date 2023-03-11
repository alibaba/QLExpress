package com.alibaba.qlexpress4.runtime.data.instruction;

/**
 * @Author TaoKan
 * @Date 2023/1/25 上午11:20
 */
public class LikeState {
    private LikeStateWord info;
    private LikeState prev;
    private LikeState next;
    private int index;

    private boolean isCurrentLikePatternEquals(LikeWordPatternEnum likeWordPatternEnum){
        return this.info.getLikeWordPatternEnum().equals(likeWordPatternEnum);
    }

    private boolean isNextLikePatternEquals(LikeWordPatternEnum likeWordPatternEnum){
        return this.next().info.getLikeWordPatternEnum().equals(likeWordPatternEnum);
    }

    private boolean isCurrentCharWordIsRight(char charWord){
        return charWord != info.getCharWords().charAt(index);
    }

    private LikeStateStatus stay(){
        return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.STAY, 0);
    }

    private LikeStateStatus gotoNext(int offSet){
        return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.GOTO_NEXT, offSet);
    }

    private LikeStateStatus gotoBreak(boolean result){
        return new LikeStateStatus(result, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
    }


    public LikeStateStatus matchWords(char charWord, boolean isEndWord, int remainWords){
        if(remainWords < this.info.getCharWords().length() - index){
            return gotoBreak(false);
        }
        if("".equals(this.info.getCharWords())){
            return gotoBreak(true);
        }
        if(this.next() == null){
            //LAST
            if(isCurrentLikePatternEquals(LikeWordPatternEnum.LEFT_PERCENT_SIGN)){
                //left
                if(isCurrentCharWordIsRight(charWord)){
                    return gotoBreak(false);
                }
                index++;
                return stay();
            }else {
                //surround
                if(isCurrentCharWordIsRight(charWord)){
                    if(isEndWord){
                        //dest is end
                        return gotoBreak(false);
                    }
                    return stay();
                }else {
                    if(index == this.info.getCharWords().length() - 1){
                        //pattern is end
                        return gotoBreak(true);
                    }
                    index++;
                    return stay();
                }
            }
        }else {
            if(isCurrentLikePatternEquals(LikeWordPatternEnum.SURROUND_PERCENT_SIGN)){
                if(isNextLikePatternEquals(LikeWordPatternEnum.SURROUND_PERCENT_SIGN)){
                    //surround-surround
                    if(isEndWord){
                        //dest is end
                        return gotoBreak(false);
                    }
                    if(isCurrentCharWordIsRight(charWord)){
                        return stay();
                    }
                    index++;
                    return gotoNext(0);
                }else {
                    //surround-left
                    if(remainWords < this.next.info.getCharWords().length() + 1){
                        return gotoBreak(false);
                    }
                    if(isCurrentCharWordIsRight(charWord)){
                        if(isEndWord){
                            return gotoBreak(false);
                        }
                        return stay();
                    }
                    index++;
                    return gotoNext(remainWords - (this.next.info.getCharWords().length() + 1));
                }
            }else if(isCurrentLikePatternEquals(LikeWordPatternEnum.RIGHT_PERCENT_SIGN)){
                if(isNextLikePatternEquals(LikeWordPatternEnum.SURROUND_PERCENT_SIGN)){
                    //right-surround
                    if(isCurrentCharWordIsRight(charWord)){
                        return gotoBreak(false);
                    }
                    index++;
                    return gotoNext(0);
                }else {
                    //right-left
                    if(isCurrentCharWordIsRight(charWord)){
                        return gotoBreak(false);
                    }
                    index++;
                    int nextLength = this.next.info.getCharWords().length();
                    if(remainWords >= nextLength + 1){
                        return gotoNext(remainWords - (nextLength + 1));
                    }
                    return gotoBreak(false);
                }
            }
        }
        return gotoBreak(false);
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
