package com.alibaba.qlexpress4;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class Express4RunnerTest {

    @Test
    public void mapSetGetTest() throws Exception {
        String script = "new HashMap()";
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object result = express4Runner.execute(script, Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
        assertTrue(result instanceof HashMap);
    }

}