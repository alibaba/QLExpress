package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: TaoKan
 */
public class ParentParameters implements Parameters {
    public List<Value> values = new ArrayList<>();

    @Override
    public Value get(int i) {
        return values.get(i);
    }

    @Override
    public int size() {
        return values.size();
    }

    public void push(Object s){
        values.add(new DataValue(s));
    }
}
