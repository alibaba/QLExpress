package com.alibaba.qlexpress4.security;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:29
 */
public interface IStrategy {
    IStrategyBlackList checkBlackList();

    IStrategyWhiteList checkWhiteList();

    IStrategySandBox checkSandBox();
}
