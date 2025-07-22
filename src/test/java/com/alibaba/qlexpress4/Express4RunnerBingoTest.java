package com.alibaba.qlexpress4;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;
import com.alibaba.qlexpress4.security.StrategyIsolation;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

/**
 * @author 冰够
 */
public class Express4RunnerBingoTest {
    @Test
    public void test() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder().traceExpression(true).build());
        String script = "sellerId in [1001]\n"
            + "&& categoryId in [2001]\n"
            + "&& itemTag in [3001]\n"
            + "&& spCode == null\n"
            + "&& !((spCode != null || itemTag in [182785,103234,106306,28674]) || itemTag in [1362498] || sellerId in [2206567368008,"
            + "2206584264218,3937219703] || itemTag in [30145])\n"
            + "|| (sellerId in [12,3] || itemTag in [12,3])";

        Map<String, Object> context = new HashMap<>();
        context.put("sellerId", 1001L);
        context.put("categoryId", 2001L);
        context.put("itemTag", 3001);
        System.out.println("script = " + script);

        QLResult qlResult = express4Runner.execute(script, context, QLOptions.DEFAULT_OPTIONS);
        System.out.println("qlResult.getResult() = " + qlResult.getResult());
        Assert.assertTrue(qlResult.getExpressionTraces().get(0).isEvaluated());
    }

    @Test
    public void test_customFunction() {
        String script = "customFunction(spCodes)";
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder().traceExpression(true).build());
        Set<String> outVarNames = express4Runner.getOutVarNames(script);
        System.out.println("outVarNames = " + outVarNames);
        Map<String, Object> context = Collections.singletonMap("customFunction", (Supplier<?>)() -> true);
        QLResult qlResult = express4Runner.execute(script, context, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue((Boolean)qlResult.getResult());
    }

    /**
     * 如果是0/0-1 这种会直接报错，但是0.0/0 或者 0/0.0 直接就返回NaN了,6.0/0.0 返回的是Infinity，这种能统一一下返回结果不
     */
    @Test
    public void test_divideZero() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        QLOptions defaultOptions = QLOptions.DEFAULT_OPTIONS;
        QLOptions preciseQlOptions = QLOptions.builder().precise(true).build();

        assertThrowArithmeticException(() -> express4Runner.execute("0 / 0 - 1", Collections.emptyMap(), defaultOptions));
        assertThrowArithmeticException(() -> express4Runner.execute("0 / 0 - 1", Collections.emptyMap(), preciseQlOptions));

        QLResult qlResult = express4Runner.execute("0 / 0.0", Collections.emptyMap(), defaultOptions);
        Assert.assertEquals(Double.NaN, qlResult.getResult());
        assertThrowArithmeticException(() -> express4Runner.execute("0 / 0.0", Collections.emptyMap(), preciseQlOptions));

        qlResult = express4Runner.execute("0 / 0.0 - 1", Collections.emptyMap(), defaultOptions);
        Assert.assertEquals(Double.NaN, qlResult.getResult());
        assertThrowArithmeticException(() -> express4Runner.execute("0 / 0.0 - 1", Collections.emptyMap(), preciseQlOptions));

        qlResult = express4Runner.execute("6.0 / 0.0 - 1", Collections.emptyMap(), defaultOptions);
        Assert.assertEquals(Double.POSITIVE_INFINITY, qlResult.getResult());
        assertThrowArithmeticException(() -> express4Runner.execute("6.0 / 0.0 - 1", Collections.emptyMap(), preciseQlOptions));

        Assert.assertThrows(ArithmeticException.class, () -> System.out.println(0 / 0));
    }

    private void assertThrowArithmeticException(ThrowingRunnable runnable) {
        QLRuntimeException qlRuntimeException = Assert.assertThrows(QLRuntimeException.class, runnable);
        Assert.assertEquals(QLErrorCodes.INVALID_ARITHMETIC.name(), qlRuntimeException.getErrorCode());
        Assert.assertEquals(ArithmeticException.class, qlRuntimeException.getCause().getClass());
    }

    @Test
    public void test_map() {
        String script = "\n"
            + "map = new HashMap();\n"
            + "map.put(\"key1\", \"value1\");\n"
            + "return map;";
        System.out.println("script = " + script);
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
            .securityStrategy(QLSecurityStrategy.open())
            .traceExpression(false).build()
        );
        // TODO 冰够 com.alibaba.qlexpress4.Express4Runner.execute(java.lang.String, java.util.Map<java.lang.String,java.lang.Object>, com.alibaba.qlexpress4.QLOptions)的context为啥不能是null，尤其在写demo的时候会传null，新老版本都存在此问题
        //QLResult qlResult = express4Runner.execute(script, (Map<String, Object>)null, QLOptions.DEFAULT_OPTIONS);
        QLResult qlResult = express4Runner.execute(script, Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
        Assert.assertNotNull(qlResult.getResult());
    }
}
