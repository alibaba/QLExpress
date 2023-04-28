package com.alibaba.qlexpress4.member;

/**
 * @Author TaoKan
 * @Date 2023/4/9 下午7:15
 */
public interface IMethod extends IMember{
    Object invoke(Object bean, Object... params) throws Throwable;


}
