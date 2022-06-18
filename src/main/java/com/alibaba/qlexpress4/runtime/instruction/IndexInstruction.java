package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.data.ArrayItemValue;
import com.alibaba.qlexpress4.runtime.data.ListItemValue;
import com.alibaba.qlexpress4.runtime.data.MapItemValue;

import java.util.List;
import java.util.Map;

/**
 * @Operation: extract value with index, like a[0], m['a']
 * @Input: 2, indexable object and index
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class IndexInstruction extends QLInstruction {

    public IndexInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        Object indexAble = qRuntime.pop().get();
        Object index = qRuntime.pop().get();
        if (indexAble instanceof List) {
            Integer indexInt = assertType(index, Integer.class, "LIST_INVALID_INDEX",
                    "list can only be indexed by int");
            qRuntime.push(new ListItemValue((List<? super Object>) indexAble, indexInt));
        } else if (indexAble instanceof Map) {
            qRuntime.push(new MapItemValue((Map<? super Object, ? super Object>) indexAble, index));
        } else if (indexAble != null && indexAble.getClass().isArray()) {
            Integer indexInt = assertType(index, Integer.class, "ARRAY_INVALID_INDEX",
                    "array can only be indexed by int");
            qRuntime.push(new ArrayItemValue(indexAble, indexInt));
        } else {
            throw errorReporter.report("INVALID_INDEX", "%s not support index",
                    indexAble == null? "null": indexAble.getClass().getName());
        }
    }

    private <T> T assertType(Object obj, Class<T> assertType, String errCode, String errMsg) {
        if (obj != null && obj.getClass().isAssignableFrom(obj.getClass())) {
            return assertType.cast(obj);
        }
        throw errorReporter.report(errCode, errMsg);
    }
}
