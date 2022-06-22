package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.test.property.ParentClass;

/**
 * @Author TaoKan
 * @Date 2022/5/15 上午11:12
 */
public class TestParametersParentClass implements Parameters {
    @Override
    public Value get(int i) {
        ParentClass parent = new ParentClass();
        return parent;
    }
}
