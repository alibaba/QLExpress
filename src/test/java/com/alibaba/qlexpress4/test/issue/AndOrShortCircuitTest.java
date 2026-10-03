package com.alibaba.qlexpress4.test.issue;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import org.junit.Test;
import java.util.Collections;
import static org.junit.Assert.*;

public class AndOrShortCircuitTest {

    private final Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

    @Test
    public void and_shortCircuit_shouldNotEvaluateRightWhenLeftIsFalse() {
        // "1/0" would throw if evaluated; "and" should short-circuit like "&&"
        assertFalse((Boolean) runner.execute("false and (1/0)",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
    }

    @Test
    public void or_shortCircuit_shouldNotEvaluateRightWhenLeftIsTrue() {
        // "1/0" would throw if evaluated; "or" should short-circuit like "||"
        assertTrue((Boolean) runner.execute("true or (1/0)",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
    }

    @Test
    public void and_correctness() {
        assertTrue((Boolean) runner.execute("true and true",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        assertFalse((Boolean) runner.execute("true and false",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        assertFalse((Boolean) runner.execute("false and true",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        assertFalse((Boolean) runner.execute("false and false",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
    }

    @Test
    public void or_correctness() {
        assertTrue((Boolean) runner.execute("true or true",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        assertTrue((Boolean) runner.execute("true or false",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        assertTrue((Boolean) runner.execute("false or true",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        assertFalse((Boolean) runner.execute("false or false",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
    }

    @Test
    public void mixed_and_or_shortCircuit() {
        // "&&" and "and" mixed
        assertFalse((Boolean) runner.execute("false and true and (1/0)",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        // "||" and "or" mixed
        assertTrue((Boolean) runner.execute("false or true or (1/0)",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        // cross: "and" with "||"
        assertTrue((Boolean) runner.execute("(false and (1/0)) || true",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        // cross: "or" with "&&"
        assertFalse((Boolean) runner.execute("(true or (1/0)) && false",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
    }

    @Test
    public void and_eagerEvaluation_whenLeftIsTrue() {
        // When left is true, right MUST be evaluated (no short-circuit)
        assertTrue((Boolean) runner.execute("true and (1 > 0)",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
    }

    @Test
    public void or_eagerEvaluation_whenLeftIsFalse() {
        // When left is false, right MUST be evaluated (no short-circuit)
        assertTrue((Boolean) runner.execute("false or (1 > 0)",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
    }
}
