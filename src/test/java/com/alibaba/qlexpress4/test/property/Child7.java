package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.annotation.QLAlias;

/**
 * @Author TaoKan
 * @Date 2022/7/31 上午11:30
 */
public class Child7 {
    @QLAlias("测试静态字段")
    public static final int t = 8;

    @QLAlias("测试字段")
    public int ts = 9;

    @QLAlias("测试方法")
    public int getSide(){
        return 10;
    }

    @QLAlias("测试静态方法")
    public static int getSii(){
        return 11;
    }
}
