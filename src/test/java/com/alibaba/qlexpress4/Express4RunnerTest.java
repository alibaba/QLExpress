package com.alibaba.qlexpress4;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Test
    public void parseLambdaTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        List<Integer> result = Stream.of(1, 2, 3)
                .filter(express4Runner.parseToLambda("false", Collections.emptyMap(),
                        QLOptions.DEFAULT_OPTIONS))
                .collect(Collectors.toList());
        assertTrue(result.isEmpty());
    }
}