package com.alibaba.qlexpress4.test.qlalias;

import com.alibaba.qlexpress4.annotation.QLAlias;

@QLAlias("用户")
public class User {
    
    @QLAlias("是vip")
    private boolean vip;
    
    @QLAlias("用户名")
    private String name;
    
    public boolean isVip() {
        return vip;
    }
    
    public void setVip(boolean vip) {
        this.vip = vip;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
