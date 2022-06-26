package com.alibaba.qlexpress4.runtime.data.process;

import com.alibaba.qlexpress4.utils.BasicUtil;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午11:22
 */
public class Weighter {
    private final int assignLevel;

    public Weighter(){
        this.assignLevel = 0;
    }

    public Weighter(int assignLevel){
        this.assignLevel = assignLevel;
    }

    public int addWeight(int weight){
        return weight + BasicUtil.LEVEL_FACTOR * this.assignLevel;
    }
}
