package com.alibaba.qlexpress4.test.performance;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;

import org.junit.Test;

/**
 * @author 冰够
 */
public class QLExpress4PerformanceTest {
    private static final Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
    private static final AtomicLong notInOperatorTimeCostNs = new AtomicLong(0);
    private static final AtomicLong executeTimeCostNs = new AtomicLong(0);

    static {
express4Runner.addOperator("not_in", (left, right) -> {
    long start = System.nanoTime();
    try {
        Object leftValue = left.get();
        Collection<?> rightCollection = (Collection<?>)right.get();
        return rightCollection == null || !rightCollection.contains(leftValue);
    } finally {
        long end = System.nanoTime();
        notInOperatorTimeCostNs.addAndGet(end - start);
    }
});
    }

    /**
     * 平均耗时信息
     * not_in operator avg time cost:914ns
     * execute avg time cost:213us
     */
    @Test
    public void test_short_collection() {
        String expression = "sellerId not_in [3438]";
        execute(expression, Collections.singletonMap("sellerId", 2025L), 100L);
    }

    /**
     * 平均耗时信息
     * not_in operator avg time cost:1468ns
     * execute avg time cost:3239us
     */
    @Test
    public void test_long_collection() {
        String expression = "sellerId not_in [3438,8287,10620,10663,11175,11740,15670,16724,17692,18198,19362,21054,23139,23743,24561,29708,29848,32330,32833,33366,35044,41363,41387,44712,46066,47147,48580,50706,52144,52150,52402,54712,59575,62492,72218,74553,74901,79447,79459,81348,83046,86399,87885,89547,90600,90609,93714,94507,97043,99115]";
        execute(expression, Collections.singletonMap("sellerId", 2025L), 100L);
    }

    private void execute(String expression, Map<String, Object> context, Long count) {
        // 预热5次
        for (int i = 0; i < 5; i++) {
            Object result = express4Runner.execute(expression, context, QLOptions.DEFAULT_OPTIONS);
            System.out.println("result = " + result);
        }

        // 重置
        reset();

        // 正式执行
        for (int i = 0; i < count; i++) {
            long start = System.nanoTime();
            express4Runner.execute(expression, context, QLOptions.DEFAULT_OPTIONS);
            long end = System.nanoTime();
            executeTimeCostNs.addAndGet(end - start);
        }

        // 打印结果
        System.out.printf("not_in operator avg time cost:%sns%n", notInOperatorTimeCostNs.get() / count);
        System.out.printf("execute avg time cost:%sus%n", executeTimeCostNs.get() / count / 1000);
    }

    private void reset() {
        notInOperatorTimeCostNs.set(0);
        executeTimeCostNs.set(0);
    }
}
