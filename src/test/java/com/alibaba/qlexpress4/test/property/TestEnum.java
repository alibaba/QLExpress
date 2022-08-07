package com.alibaba.qlexpress4.test.property;

/**
 * @Author TaoKan
 * @Date 2022/7/10 下午4:43
 */
public enum TestEnum {
    SKT(-1),
    KSY(1);

    private final int value;

    TestEnum(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }

}
