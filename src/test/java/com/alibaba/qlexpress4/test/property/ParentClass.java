package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * Author: TaoKan
 */
public class ParentClass implements Value {
    @Override
    public Object get() {
        Parent parent = new Parent();
        parent.setAge(35);
        return parent.getClass();
    }
}
