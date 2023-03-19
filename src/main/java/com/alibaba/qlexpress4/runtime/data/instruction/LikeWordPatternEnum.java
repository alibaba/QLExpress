package com.alibaba.qlexpress4.runtime.data.instruction;

/**
 * @Author TaoKan
 * @Date 2023/3/5 下午7:59
 */
public enum LikeWordPatternEnum {
    //0,1,2,3
    LEFT_PERCENT_SIGN(0), RIGHT_PERCENT_SIGN(1), SURROUND_PERCENT_SIGN(2), NONE(3);


    LikeWordPatternEnum(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }

}
