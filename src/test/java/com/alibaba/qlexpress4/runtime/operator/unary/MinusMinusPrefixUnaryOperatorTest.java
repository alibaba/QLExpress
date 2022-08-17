package com.alibaba.qlexpress4.runtime.operator.unary;

import java.math.BigInteger;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.MockErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.MockValue;

import junit.framework.TestCase;
import org.junit.Assert;

public class MinusMinusPrefixUnaryOperatorTest extends TestCase {
    public void testExecute() {
        ErrorReporter errorReporter = new MockErrorReporter();
        MinusMinusPrefixUnaryOperator minusMinusPrefixUnaryOperator = MinusMinusPrefixUnaryOperator.getInstance();

        Value value = new MockValue(10L, null);
        Object result = minusMinusPrefixUnaryOperator.execute(value, errorReporter);
        Assert.assertEquals(10L, result);

        value = new MockValue(new BigInteger("10"), BigInteger.class);
        BigInteger bigIntegerResult = (BigInteger)minusMinusPrefixUnaryOperator.execute(value, errorReporter);
        Assert.assertEquals(10L, bigIntegerResult.intValue());
    }
}