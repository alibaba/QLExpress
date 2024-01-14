package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;

/**
 * Author: TaoKan
 */
public class MockCastParameters implements Parameters {
    private Class<?> aClass;
    private Object obj;

    public MockCastParameters(Class<?> aClass, Object obj){
        this.aClass = aClass;
        this.obj = obj;
    }


    @Override
    public Value get(int i) {
        if(i == 0){
            return new DataValue(aClass);
        }
        if(i == 1){
            return new DataValue(obj);
        }
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}