package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Author TaoKan
 * @Date 2022/5/15 上午11:13
 */
public class ParentClass implements Value {
    @Override
    public Object get() {
        Parent parent = new Parent();
        parent.setAge(35);
        return parent.getClass();
    }

    @Override
    public Class<?> getDefineType() {
        return Parent.class;
    }
}
