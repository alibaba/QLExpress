package com.alibaba.qlexpress4.runtime.operator.unary;

import java.math.BigInteger;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.MockErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.MockValue;

import junit.framework.TestCase;
import org.junit.Assert;

public class MinusMinusSuffixUnaryOperatorTest extends TestCase {
    public void testExecute() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MinusMinusSuffixUnaryOperator minusMinusSuffixUnaryOperator = new MinusMinusSuffixUnaryOperator();

        Value value = new MockValue(10L, null);
        Object result = minusMinusSuffixUnaryOperator.execute(value, errorReporter);
        Assert.assertEquals(9L, result);

        BigInteger bigInteger = BigInteger.valueOf(10L);
        value = new MockValue(bigInteger, BigInteger.class);
        BigInteger bigIntegerResult = (BigInteger)minusMinusSuffixUnaryOperator.execute(value, errorReporter);
        Assert.assertEquals(9L, bigIntegerResult.longValue());
    }
}