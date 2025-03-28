package com.alibaba.qlexpress4;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

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
}
