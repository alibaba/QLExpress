package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.exception.QLException;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class Express4RunnerTest {

    @Test
    public void mapSetGetTest() {
        String script = "a = new HashMap<>();" +
                "a['aaa'] = 'bbb';" +
                "a";
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object result = express4Runner.execute(script, Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
        assertTrue(result instanceof HashMap);
        assertEquals("bbb", ((HashMap<?, ?>) result).get("aaa"));
    }

    @Test
    public void shortCircuitTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        assertTrue((Boolean) express4Runner.execute("true && true && true",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));
        assertFalse((Boolean) express4Runner.execute("true && false && (1/0)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));
        assertTrue((Boolean) express4Runner.execute("a = 1+1+1+1+1+1+1+1+1;" +
                        "true && true && true",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));

        assertFalse((Boolean) express4Runner.execute("false || false || false",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));
        assertTrue((Boolean) express4Runner.execute("false || true || (1/0)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));
        assertTrue((Boolean) express4Runner.execute("(false && (1/0)) || true || (1/0)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));

        assertErrorCode(express4Runner, "true && (1/0)", "INVALID_ARITHMETIC");
    }

    @Test
    public void assignTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        assertErrorCode(express4Runner, "1 = 0", "INVALID_ASSIGN_TARGET");
    }

    @Test
    public void debugExample() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        QLOptions debugOptions = QLOptions.builder()
                .debug(true)
                .build();
        Object result = express4Runner.execute("1+1", Collections.emptyMap(), debugOptions);
        assertEquals(2, result);

        Object result1 = express4Runner.execute("false || true || (1/0)",
                Collections.emptyMap(), debugOptions);
        assertTrue((Boolean) result1);
    }

    @Test
    public void populateTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        QLOptions populateOption = QLOptions.builder()
                .polluteUserContext(true).build();
        Map<String, Object> populatedMap = new HashMap<>();
        populatedMap.put("b", 10);
        express4Runner.execute("a = 11;b = a", populatedMap, populateOption);
        assertEquals(11, populatedMap.get("a"));
        assertEquals(11, populatedMap.get("b"));

        // no population
        Map<String, Object> populatedMap2 = new HashMap<>();
        express4Runner.execute("a = 11", populatedMap2, QLOptions.DEFAULT_OPTIONS);
        assertFalse(populatedMap2.containsKey("a"));

        Map<String, Object> populatedMap3 = new HashMap<>();
        populatedMap3.put("a", 10);
        assertEquals(19, express4Runner
                .execute("a = 19;a", populatedMap3, QLOptions.DEFAULT_OPTIONS));
        assertEquals(10, populatedMap3.get("a"));
    }

    @Test
    public void mapLiteralTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> result = (Map<String, Object>) express4Runner
                .execute("{a:123,'b':'test'}", new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
        assertEquals(123, result.get("a"));
        assertEquals("test", result.get("b"));
    }

    private void assertErrorCode(Express4Runner express4Runner, String script, String errCode) {
        try {
            express4Runner.execute(script, Collections.emptyMap(),
                    QLOptions.DEFAULT_OPTIONS);
        } catch (QLException e) {
            assertEquals(errCode, e.getErrorCode());
        }
    }

    private void assertErrorCode(Express4Runner express4Runner, Map<String, Object> existMap,
                                 String script, String errCode) {
        try {
            express4Runner.execute(script, existMap, QLOptions.DEFAULT_OPTIONS);
            fail("no errCode:" + errCode);
        } catch (QLException e) {
            assertEquals(errCode, e.getErrorCode());
        }
    }
}