package com.ql.util.express.issue;

import java.util.Arrays;
import java.util.List;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.exception.QLException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author 冰够
 */
public class Issue247Test {
    @Test
    public void test() throws Exception {
        boolean compareNullLessMoreAsFalse = QLExpressRunStrategy.isCompareNullLessMoreAsFalse();
        try {
            IExpressContext<String, Object> context = new DefaultContext<>();
            ExpressRunner runner = new ExpressRunner(false, true);

            boolean result = (boolean)runner.execute("null == null", context, null, false, false);
            Assert.assertTrue(result);

            result = (boolean)runner.execute("null != null", context, null, false, false);
            Assert.assertFalse(result);

            List<String> expressionList = Arrays.asList(
                "null > 2",
                "null >= 2",
                "null < 2",
                "null <= 2",
                "2 > null",
                "2 >= null",
                "2 < null",
                "2 <= null"
            );

            for (String expression : expressionList) {
                try {
                    runner.execute(expression, context, null, false, false);
                    Assert.fail(expression);
                } catch (QLException e) {
                }
            }

            QLExpressRunStrategy.setCompareNullLessMoreAsFalse(true);
            for (String expression : expressionList) {
                result = (boolean)runner.execute(expression, context, null, false, false);
                Assert.assertFalse(result);
            }
        } finally {
            QLExpressRunStrategy.setCompareNullLessMoreAsFalse(compareNullLessMoreAsFalse);
        }
    }

    @Test
    public void test_java_compare() {
        Long l1 = null;
        Long l2 = null;
        Assert.assertTrue(l1 == l2);
        Assert.assertFalse(l1 != l2);
    }

    @Test(expected = NullPointerException.class)
    public void test_null_java_compare_more() {
        boolean result = ((Long)null) > ((Long)null);
        System.out.println("result = " + result);
    }

    @Test(expected = NullPointerException.class)
    public void test_null_java_compare_more_equal() {
        boolean result = ((Long)null) >= ((Long)null);
        System.out.println("result = " + result);
    }

    @Test(expected = NullPointerException.class)
    public void test_null_java_compare_less() {
        boolean result = ((Long)null) < ((Long)null);
        System.out.println("result = " + result);
    }

    @Test(expected = NullPointerException.class)
    public void test_null_java_compare_less_equal() {
        boolean result = ((Long)null) <= ((Long)null);
        System.out.println("result = " + result);
    }
}
