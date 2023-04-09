package com.alibaba.qlexpress4.security;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午11:09
 */
public class QLStrategySandBox extends StrategySandBox {

    public QLStrategySandBox(boolean isSandBoxMode) {
        super(isSandBoxMode);
    }

    @Override
    public boolean checkInRules() {
        if(this.isSandBoxMode()){
            //
        }
        return false;
    }
}
