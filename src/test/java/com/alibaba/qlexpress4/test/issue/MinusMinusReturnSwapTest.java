package com.alibaba.qlexpress4.test.issue;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class MinusMinusReturnSwapTest {

    private final Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

    @Test
    public void prefixMinusMinus_shouldReturnNewValue() {
        Object result = runner.execute("a = 5; b = --a; b;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(4, result);
    }

    @Test
    public void prefixMinusMinus_variableShouldBeDecremented() {
        Object result = runner.execute("a = 5; --a; a;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(4, result);
    }

    @Test
    public void suffixMinusMinus_shouldReturnOldValue() {
        Object result = runner.execute("a = 5; b = a--; b;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(5, result);
    }

    @Test
    public void suffixMinusMinus_variableShouldBeDecremented() {
        Object result = runner.execute("a = 5; a--; a;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(4, result);
    }

    @Test
    public void prefixPlusPlus_shouldReturnNewValue() {
        Object result = runner.execute("a = 5; b = ++a; b;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(6, result);
    }

    @Test
    public void suffixPlusPlus_shouldReturnOldValue() {
        Object result = runner.execute("a = 5; b = a++; b;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(5, result);
    }

    @Test
    public void prefixMinusMinus_inExpression_shouldUseNewValue() {
        Object result = runner.execute("a = 10; --a + 1;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(10, result);
    }

    @Test
    public void suffixMinusMinus_inExpression_shouldUseOldValue() {
        Object result = runner.execute("a = 10; a-- + 1;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(11, result);
    }
}
