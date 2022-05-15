package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.test.property.Parent;

/**
 * @Author TaoKan
 * @Date 2022/5/15 上午11:00
 */
public class TestParametersParent implements Parameters {
    @Override
    public Value get(int i) {
        Parent parent = new Parent();
        parent.setAge(35);
        return parent;
    }
}
