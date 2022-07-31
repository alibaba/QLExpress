package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/6/15 下午4:38
 */
public class ParentParameters implements Parameters {
    public List<Value> values = new ArrayList<>();

    @Override
    public Value get(int i) {
        return values.get(i);
    }

    @Override
    public int size() {
        return 0;
    }

    public void push(Object s){
        values.add(new DataValue(s));
    }
}
