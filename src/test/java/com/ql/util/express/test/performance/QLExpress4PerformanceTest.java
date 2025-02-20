package com.ql.util.express.test.performance;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.Operator;
import org.junit.Test;

/**
 * @author 冰够
 */
public class QLExpress4PerformanceTest {
    private static final ExpressRunner expressRunner = new ExpressRunner();
    private static final AtomicLong notInOperatorTimeCostNs = new AtomicLong(0);
    private static final AtomicLong executeTimeCostNs = new AtomicLong(0);

    static {
        try {
            expressRunner.addOperator("not_in", new Operator() {
                @Override
                public Object executeInner(Object[] list) throws Exception {
                    long start = System.nanoTime();
                    try {
                        Object leftValue = list[0];
                        List<?> rightList = Arrays.asList((Object[])list[1]);
                        return !rightList.contains(leftValue);
                    } finally {
                        long end = System.nanoTime();
                        notInOperatorTimeCostNs.addAndGet(end - start);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 平均耗时信息
     * not_in operator avg time cost:492ns
     * execute avg time cost:11us
     */
    @Test
    public void test_short_collection() throws Exception {
        String expression = "sellerId not_in [3438]";
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("sellerId", 2025L);
        execute(expression, context, 100L);
    }

    /**
     * 平均耗时信息
     * not_in operator avg time cost:1445ns
     * execute avg time cost:18us
     */
    @Test
    public void test_long_collection() throws Exception {
        String expression = "sellerId not_in [3438,8287,10620,10663,11175,11740,15670,16724,17692,18198,19362,21054,23139,23743,24561,29708,29848,32330,32833,33366,35044,41363,41387,44712,46066,47147,48580,50706,52144,52150,52402,54712,59575,62492,72218,74553,74901,79447,79459,81348,83046,86399,87885,89547,90600,90609,93714,94507,97043,99115]";
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("sellerId", 2025L);
        execute(expression, context, 100L);
    }

    private void execute(String expression, IExpressContext<String, Object> context, Long count) throws Exception {
        InstructionSet instructionSet = expressRunner.parseInstructionSet(expression);
        System.out.println("instructionSet = " + instructionSet);

        // 预热5次
        for (int i = 0; i < 5; i++) {
            Object result = expressRunner.execute(expression, context, null, true, false);
            System.out.println("result = " + result);
        }

        // 重置
        reset();

        // 正式执行
        for (int i = 0; i < count; i++) {
            long start = System.nanoTime();
            expressRunner.execute(expression, context, null, true, false);
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
