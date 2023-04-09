package com.alibaba.qlexpress4.security;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午11:02
 */
public abstract class StrategySandBox implements IStrategy {
    private boolean isSandBoxMode;

    public StrategySandBox(boolean isSandBoxMode) {
        this.isSandBoxMode = isSandBoxMode;
    }

    public abstract boolean checkInRules();

    protected boolean isSandBoxMode() {
        return isSandBoxMode;
    }

}
