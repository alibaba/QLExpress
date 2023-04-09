package com.alibaba.qlexpress4.security;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:37
 */
public class StrategyStruct {
    private final Class<?> clazz;
    private final String name;

    public StrategyStruct(Class<?> clazz){
        this.clazz = clazz;
        this.name = "";
    }

    public StrategyStruct(Class<?> clazz, String name){
        this.clazz = clazz;
        this.name = name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

}
