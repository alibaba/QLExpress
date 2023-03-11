package com.ql.util.express;

/**
 * @Author TaoKan
 * @Date 2023/1/25 上午11:20
 */
public class LikeState {
    private LikeStateWord info;
    private LikeState prev;
    private LikeState next;
    private int index;


    public LikeStateStatus matchWords(char charWord, boolean isEndWord, int remainWords){
        if(remainWords < this.info.getCharWords().length() - index){
            return new LikeStateStatus(false, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
        }
        if("".equals(this.info.getCharWords())){
            return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
        }
        if(this.next() == null){
            //LAST
            if(this.info.getLikeWordPatternEnum().equals(LikeWordPatternEnum.LEFT_PERCENT_SIGN)){
                //left
                if(charWord != info.getCharWords().charAt(index)){
                    return new LikeStateStatus(false, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
                }else {
                    index++;
                    return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.STAY, 0);
                }
            }else {
                //surround
                if(charWord != info.getCharWords().charAt(index)){
                    if(isEndWord){
                        //dest is end
                        return new LikeStateStatus(false, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
                    }else {
                        return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.STAY, 0);
                    }
                }else {
                    if(index == this.info.getCharWords().length() - 1){
                        //pattern is end
                        return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
                    }
                    index++;
                    return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.STAY, 0);
                }
            }
        }else {
            if(this.info.getLikeWordPatternEnum().equals(LikeWordPatternEnum.SURROUND_PERCENT_SIGN)){
                if(this.next.info.getLikeWordPatternEnum().equals(LikeWordPatternEnum.SURROUND_PERCENT_SIGN)){
                    //surround-surround
                    if(isEndWord){
                        //dest is end
                        return new LikeStateStatus(false, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
                    }else {
                        if(charWord != info.getCharWords().charAt(index)){
                            return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.STAY, 0);
                        }else {
                            index++;
                            return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.GOTO_NEXT, 0);
                        }
                    }
                }else {
                    //surround-left
                    if(remainWords < this.next.info.getCharWords().length() + 1){
                        return new LikeStateStatus(false, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
                    }
                    if(charWord != info.getCharWords().charAt(index)){
                        if(isEndWord){
                            return new LikeStateStatus(false, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
                        }
                        return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.STAY, 0);
                    }else {
                        index++;
                        int nextLength = this.next.info.getCharWords().length();
                        return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.GOTO_NEXT, remainWords - (nextLength + 1));

                    }
                }
            }else if(this.info.getLikeWordPatternEnum().equals(LikeWordPatternEnum.RIGHT_PERCENT_SIGN)){
                if(this.next.info.getLikeWordPatternEnum().equals(LikeWordPatternEnum.SURROUND_PERCENT_SIGN)){
                    //right-surround
                    if(charWord != info.getCharWords().charAt(index)){
                        return new LikeStateStatus(false, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
                    }else {
                        index++;
                        return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.GOTO_NEXT, 0);
                    }
                }else {
                    //right-left
                    if(charWord != info.getCharWords().charAt(index)){
                        return new LikeStateStatus(false, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
                    }else {
                        index++;
                        int nextLength = this.next.info.getCharWords().length();
                        if(remainWords >= nextLength + 1){
                            return new LikeStateStatus(true, LikeStateStatus.LikeStateStatusEnum.GOTO_NEXT, remainWords - (nextLength + 1));
                        }
                        return new LikeStateStatus(false, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
                    }
                }
            }
        }

        return new LikeStateStatus(false, LikeStateStatus.LikeStateStatusEnum.BREAK, 0);
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
