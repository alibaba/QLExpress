package com.alibaba.qlexpress4.runtime.data.implicit;

/**
 * @Author TaoKan
 * @Date 2022/7/3 上午9:09
 */
public class QLConvertResult {

    private final QLConvertResultType resultType;
    private final Object castValue;

    public QLConvertResult(QLConvertResultType resultType, Object castValue){
        this.resultType = resultType;
        this.castValue = castValue;
    }

    public QLConvertResultType getResultType(){
        return this.resultType;
    }

    public Object getCastValue() {
        return castValue;
    }

}

