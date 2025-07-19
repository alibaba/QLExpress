package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.test.property.Parent;

/**
 * Author: TaoKan
 */
public class MockParametersParent implements Parameters {
    @Override
    public Value get(int i) {
        Parent parent = new Parent();
        parent.setAge(35);
        return parent;
    }
    
    @Override
    public int size() {
        return 0;
    }
}
