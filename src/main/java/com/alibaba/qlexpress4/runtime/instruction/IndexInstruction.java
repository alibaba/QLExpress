package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.ArrayItemValue;
import com.alibaba.qlexpress4.runtime.data.ListItemValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Operation: extract value with index, like a[0], m['a']
 * @Input: 2, indexable object and index
 * @Output: 1
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
            Integer indexInt = assertType(index, Integer.class, "LIST_INVALID_INDEX",
                    "list can only be indexed by int");
            qContext.push(new ListItemValue((List<? super Object>) indexAble, indexInt));
        } else if (indexAble instanceof Map) {
            qContext.push(new MapItemValue((Map<?, ?>) indexAble, index));
        } else if (indexAble != null && indexAble.getClass().isArray()) {
            Integer indexInt = assertType(index, Integer.class, "ARRAY_INVALID_INDEX",
                    "array can only be indexed by int");
            qContext.push(new ArrayItemValue(indexAble, indexInt));
        } else {
            throw errorReporter.report("INVALID_INDEX", "%s not support index",
                    indexAble == null? "null": indexAble.getClass().getName());
        }
        return QResult.CONTINUE_RESULT;
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
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "Index", debug);
    }

    private <T> T assertType(Object obj, Class<T> assertType, String errCode, String errMsg) {
        if (obj != null && obj.getClass().isAssignableFrom(obj.getClass())) {
            return assertType.cast(obj);
        }
        throw errorReporter.report(errCode, errMsg);
    }
}
