//package com.alibaba.qlexpress4.runtime.operator.compare;
//
//import com.alibaba.qlexpress4.QLOptions;
//import com.alibaba.qlexpress4.exception.ErrorReporter;
//import com.alibaba.qlexpress4.runtime.Value;
//import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;
//
///**
// * 1. 是否支持Character类型的比较 com.ql.util.express.BinaryOperator#objectEquals(java.lang.Object, java.lang.Object)
// * 2. 是否支持实现Comparable接口的对象比较 groovy支持
// * 3. boolean, Date, String 类型比较
// * 4. null 如何比较 org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation#compareToWithEqualityCheck
// * "a" > 10，异常
// * "ab" > 10，异常
// * "a" == 10， false
// * "a" != 10, true
// *
// * @author bingo
// */
//public abstract class CompareOperator extends BaseBinaryOperator {
//    @Override
//    public Object execute(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter) {
//        int compareResult = compare(left, right, errorReporter);
//        return execute(compareResult);
//    }
//
//    /**
//     * @param compareResult
//     * @return
//     */
//    protected abstract boolean execute(int compareResult);
//}
