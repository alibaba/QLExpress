package com.alibaba.qlexpress4.runtime.data.implicit;

import com.alibaba.qlexpress4.utils.BasicUtil;

/**
 * @Author TaoKan
 * @Date 2022/6/29 下午4:18
 */
public class QLWeighter {
    private final int assignLevel;

    public QLWeighter(){
        this.assignLevel = 0;
    }

    public QLWeighter(int assignLevel){
        this.assignLevel = assignLevel;
    }

    public int addWeight(int weight){
        return weight + BasicUtil.LEVEL_FACTOR * this.assignLevel;
    }
}
