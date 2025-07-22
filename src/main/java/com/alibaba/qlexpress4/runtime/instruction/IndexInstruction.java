package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.ArrayItemValue;
import com.alibaba.qlexpress4.runtime.data.ListItemValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;
import com.alibaba.qlexpress4.runtime.util.ValueUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Operation: extract value with index, like a[0], m['a']
 * Input: 2, indexable object and index
 * Output: 1
 * <p>
 * Author: DQinYuan
 */
public class IndexInstruction extends QLInstruction {
    
    public IndexInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Object index = qContext.pop().get();
        Object indexAble = qContext.pop().get();
        if (indexAble instanceof List) {
            Number indexNumber = ValueUtils.assertType(index,
                Number.class,
                QLErrorCodes.INVALID_INDEX.name(),
                QLErrorCodes.INVALID_INDEX.getErrorMsg(),
                errorReporter);
            List<? super Object> list = (List<? super Object>)indexAble;
            int intIndex = ValueUtils.javaIndex(list.size(), indexNumber.intValue());
            if (intIndex < 0 || intIndex >= list.size()) {
                throw errorReporter.report(QLErrorCodes.INDEX_OUT_BOUND.name(),
                    QLErrorCodes.INDEX_OUT_BOUND.getErrorMsg());
            }
            qContext.push(new ListItemValue(list, intIndex));
        }
        else if (indexAble instanceof Map) {
            qContext.push(new MapItemValue((Map<?, ?>)indexAble, index));
        }
        else if (indexAble != null && indexAble.getClass().isArray()) {
            Number indexNumber = ValueUtils.assertType(index,
                Number.class,
                QLErrorCodes.INVALID_INDEX.name(),
                QLErrorCodes.INVALID_INDEX.getErrorMsg(),
                errorReporter);
            int arrLen = Array.getLength(indexAble);
            int intIndex = ValueUtils.javaIndex(arrLen, indexNumber.intValue());
            if (intIndex < 0 || intIndex >= arrLen) {
                throw errorReporter.report(QLErrorCodes.INDEX_OUT_BOUND.name(),
                    QLErrorCodes.INDEX_OUT_BOUND.getErrorMsg());
            }
            qContext.push(new ArrayItemValue(indexAble, intIndex));
        }
        else if (indexAble == null && qlOptions.isAvoidNullPointer()) {
            qContext.push(Value.NULL_VALUE);
        }
        else {
            throw errorReporter.reportFormat(QLErrorCodes.NONINDEXABLE_OBJECT.name(),
                QLErrorCodes.NONINDEXABLE_OBJECT.getErrorMsg(),
                indexAble == null ? "null" : indexAble.getClass().getName());
        }
        return QResult.NEXT_INSTRUCTION;
    }
    
    @Override
    public int stackInput() {
        return 2;
    }
    
    @Override
    public int stackOutput() {
        return 1;
    }
    
    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": Index", debug);
    }
}
