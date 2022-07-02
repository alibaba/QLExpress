package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.exception.QLException;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

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

    private void assertErrorCode(Express4Runner express4Runner, String script, String errCode) {
        try {
            express4Runner.execute(script, Collections.emptyMap(),
                    QLOptions.DEFAULT_OPTIONS);
        } catch (QLException e) {
            assertEquals(errCode, e.getErrorCode());
        }

    }

}