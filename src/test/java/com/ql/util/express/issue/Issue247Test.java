package com.ql.util.express.issue;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.config.QLExpressRunStrategy;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author 冰够
 */
public class Issue247Test {
    private static boolean compareNullLessMoreAsFalse;

    @BeforeClass
    public static void beforeClass() {
        compareNullLessMoreAsFalse = QLExpressRunStrategy.isCompareNullLessMoreAsFalse();
        QLExpressRunStrategy.setCompareNullLessMoreAsFalse(true);
    }

    @AfterClass
    public static void afterClass() {
        QLExpressRunStrategy.setCompareNullLessMoreAsFalse(compareNullLessMoreAsFalse);
    }

    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        String expression = "2 > null";

        IExpressContext<String, Object> context = new DefaultContext<>();
        Object result = runner.execute(expression, context, null, false, false);
        Assert.assertFalse((Boolean)result);
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
