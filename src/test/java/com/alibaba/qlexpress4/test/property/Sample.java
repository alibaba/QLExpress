package com.alibaba.qlexpress4.test.property;

/**
 * @Author TaoKan
 * @Date 2022/10/12 上午10:02
 */
public class Sample {

    private int count;

    public Sample(int count){
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int notAllow(){
        return 1;
    }

}
