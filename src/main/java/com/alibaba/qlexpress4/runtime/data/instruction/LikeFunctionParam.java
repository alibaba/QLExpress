package com.alibaba.qlexpress4.runtime.data.instruction;

/**
 * @Author TaoKan
 * @Date 2023/3/19 下午4:05
 */
public class LikeFunctionParam {
    private final String dest;
    private final String resultPattern;

    public LikeFunctionParam(String dest, String resultPattern) {
        this.dest = dest;
        this.resultPattern = resultPattern;
    }

    public String getDest() {
        return dest;
    }

    public String getResultPattern() {
        return resultPattern;
    }

}
