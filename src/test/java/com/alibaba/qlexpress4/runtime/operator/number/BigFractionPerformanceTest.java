package com.alibaba.qlexpress4.runtime.operator.number;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

/**
 * <case1>
 * 100w次
 * BigDecimal cost:2886ms
 * BigFraction cost:3480ms
 * (cost2 - cost1) / cost1 = 0.2058212
 * </case1>
 *
 * <case2>
 * 1000w次
 * BigDecimal cost:23822ms
 * BigFraction cost:34632ms
 * (cost2 - cost1) / cost1 = 0.45378223
 * Process finished with exit code 0
 * </case2>
 *
 * <case3>
 * 1000w次
 * BigDecimal cost:23624ms
 * BigFraction cost:33954ms
 * (cost2 - cost1) / cost1 = 0.43726718
 * </case3>
 *
 * <case4>
 * 1亿次
 * BigDecimal cost:278811ms
 * BigFraction cost:407032ms
 * (cost2 - cost1) / cost1 = 0.459885
 * </case4>
 *
 * <case5>
 * 1000w次
 * BigFraction cost:48352ms
 * BigDecimal cost:35913ms
 * (cost2 - cost1) / cost1 = 0.34636483
 * </case5>
 *
 * @author 冰够
 */
public class BigFractionPerformanceTest {
    @Test
    public void test() {
        Random random = new Random();
        int count = 10000000 + 2;
        List<Double> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(random.nextDouble());
        }

        long start2 = System.currentTimeMillis();
        for (int i = 0; i < count - 2; i++) {
            Double first = list.get(i);
            Double second = list.get(i + 1);
            Double third = list.get(i + 2);
            BigFraction bigFraction = new BigFraction(first)
                .multiply(new BigFraction(second))
                .divide(new BigFraction(third));
            double result = bigFraction.doubleValue();
        }
        long end2 = System.currentTimeMillis();
        long cost2 = end2 - start2;
        System.out.printf("BigFraction cost:%sms\r\n", cost2);

        long start1 = System.currentTimeMillis();
        for (int i = 0; i < count - 2; i++) {
            Double first = list.get(i);
            Double second = list.get(i + 1);
            Double third = list.get(i + 2);
            BigDecimal bigDecimal = BigDecimal.valueOf(first)
                .multiply(BigDecimal.valueOf(second))
                .divide(BigDecimal.valueOf(third), RoundingMode.HALF_UP);
            double result = bigDecimal.doubleValue();
        }
        long end1 = System.currentTimeMillis();
        long cost1 = end1 - start1;
        System.out.printf("BigDecimal cost:%sms\r\n", cost1);

        System.out.printf("(cost2 - cost1) / cost1 = %s", (cost2 - cost1) / (float)cost1);
    }
}
