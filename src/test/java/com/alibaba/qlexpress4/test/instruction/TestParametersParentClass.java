package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.test.property.ParentClass;

/**
 * Author: TaoKan
 */
public class TestParametersParentClass implements Parameters {
    @Override
    public Value get(int i) {
        ParentClass parent = new ParentClass();
        return parent;
    }

    @Override
    public int size() {
        return 0;
    }
}
