package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.data.DataValue;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class FixedSizeStackTest {
    
    @Test
    public void pushPopTest() {
        FixedSizeStack fixedSizeStack = new FixedSizeStack(4);
        fixedSizeStack.push(new DataValue(1));
        fixedSizeStack.push(new DataValue(2));
        fixedSizeStack.push(new DataValue(3));
        fixedSizeStack.push(new DataValue(4));
        assertEquals(4, fixedSizeStack.pop().get());
        assertEquals(3, fixedSizeStack.pop().get());
        fixedSizeStack.push(new DataValue(5));
        fixedSizeStack.push(new DataValue(6));
        Parameters parameters = fixedSizeStack.pop(3);
        assertEquals(2, parameters.get(0).get());
        assertEquals(5, parameters.get(1).get());
        assertEquals(6, parameters.get(2).get());
        // exceed size
        assertNull(parameters.get(3));
    }
    
}