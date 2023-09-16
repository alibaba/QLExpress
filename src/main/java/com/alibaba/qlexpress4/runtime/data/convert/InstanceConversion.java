package com.alibaba.qlexpress4.runtime.data.convert;
import com.alibaba.qlexpress4.runtime.data.checker.*;
import com.alibaba.qlexpress4.runtime.data.checker.convertchecker.*;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;


/**
 * Author: TaoKan
 */
public class InstanceConversion {

    private static final TypeConvertChecker[] typeConvertChecker;

    static {
        typeConvertChecker = new TypeConvertChecker[]{
                new QLObjectConvertChecker(),
                new QLNullConvertChecker(),
                new QLAssignableConvertChecker(),
                new QLEnumConvertChecker(),
                new QLStringConvertChecker(),
                new QLCharacterConvertChecker(),
                new QLBooleanConvertChecker(),
                new QLClassConvertChecker(),
                new QLFunctionConvertChecker(),
                new QLNumberConvertChecker(),
                new QLArrayConvertChecker(),
                new QLListConvertChecker()};
    }

    public static QLConvertResult castObject(Object value, Class<?> type) {
        for (TypeConvertChecker checker : typeConvertChecker){
            if(checker.typeCheck(value,type)){
                return (QLConvertResult)checker.typeReturn(value,type);
            }
        }
        return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);
    }

}
