package com.alibaba.qlexpress4.security;

/**
 * Author: DQinYuan
 */
public interface QLSecurityStrategy {

    boolean check(Class<?> cls, String memberName);

}
