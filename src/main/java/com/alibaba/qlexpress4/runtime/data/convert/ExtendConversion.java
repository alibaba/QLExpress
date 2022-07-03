package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author TaoKan
 * @Date 2022/6/29 下午4:08
 */
public class ExtendConversion {
    public static QLConvertResult extendNumberConvert(Number n, Class type){
        if (type == BigDecimal.class) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, NumberMath.toBigDecimal(n));
        }
        if (type == BigInteger.class) {
            return  new QLConvertResult(QLConvertResultType.CAN_TRANS, NumberMath.toBigInteger(n));
        }
        return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);

    }
}
