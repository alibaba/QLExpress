package com.alibaba.qlexpress4.runtime.data.implicit;

import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.utils.BasicUtil;

/**
 * Author: TaoKan
 */
public class QLWeighter {
    private final int assignLevel;

    public QLWeighter(){
        this.assignLevel = 1;
    }

    public QLWeighter(int assignLevel){
        this.assignLevel = assignLevel;
    }

    public int addWeight(ParametersConversion.QLMatchConverter converter){
        return BasicUtil.LEVEL_FACTOR * converter.getWeight() + this.assignLevel;
    }

    public static boolean calNeedImplicitTrans(int weight){
        Integer innerWeight = weight / BasicUtil.LEVEL_FACTOR;
        return  innerWeight == ParametersConversion.QLMatchConverter.EXTEND.getWeight()
                || innerWeight == ParametersConversion.QLMatchConverter.PRIMITIVE.getWeight()
                || innerWeight == ParametersConversion.QLMatchConverter.IMPLICIT.getWeight();
    }
}
