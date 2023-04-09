package com.alibaba.qlexpress4.member;

/**
 * @Author TaoKan
 * @Date 2023/4/9 下午7:16
 */
public interface IField extends IMember {

    void set(Object bean, Object value);

    Object get(Object bean);
}
