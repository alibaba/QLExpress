package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.exception.QLTransferException;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author TaoKan
 * @Date 2022/6/29 下午4:08
 */
public class ExtendConversion {
    public static Object extendNumberConvert(Number n, Class type, Object object){
        if (type == BigDecimal.class) {
            return NumberMath.toBigDecimal(n);
        }
        if (type == BigInteger.class) {
            return NumberMath.toBigInteger(n);
        }
        throw new QLTransferException("can not cast " + object.getClass().getName()
                + " value " + object + " to number type");
    }
}
