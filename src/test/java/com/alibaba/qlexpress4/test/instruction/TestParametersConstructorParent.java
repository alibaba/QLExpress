package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.test.property.Parent;

/**
 * @Author TaoKan
 * @Date 2022/7/3 上午11:03
 */
public class TestParametersConstructorParent implements Parameters {
    @Override
    public Value get(int i) {
        if(i == 0){

        }
        Parent parent = new Parent();
        parent.setAge(35);
        return parent;
    }

    @Override
    public int size() {
        return 0;
    }
}