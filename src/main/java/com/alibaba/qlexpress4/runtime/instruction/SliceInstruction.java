package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.util.ValueUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @Operation: slice array or list, like a[2:4], a[4:-1], a[:4], a[5:], a[:]
 * @Input: 0-2
 * @Output: 1
 * <p>
 * Author: DQinYuan
 */
public class SliceInstruction extends QLInstruction {

    public enum Mode {
        LEFT, RIGHT, BOTH, COPY
    }

    private final Mode mode;

    public SliceInstruction(ErrorReporter errorReporter, Mode mode) {
        super(errorReporter);
        this.mode = mode;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        int startInt = 0;
        int endInt = 0;
        Object indexAble = null;
        if (mode == Mode.BOTH) {
            Object end = qContext.pop().get();
            Object start = qContext.pop().get();
            indexAble = qContext.pop().get();
            startInt = ValueUtils.assertType(start, Number.class, QLErrorCodes.INVALID_INDEX.name(),
                    QLErrorCodes.INVALID_INDEX.getErrorMsg(), errorReporter).intValue();
            endInt = ValueUtils.assertType(end, Number.class, QLErrorCodes.INVALID_INDEX.name(),
                    QLErrorCodes.INVALID_INDEX.getErrorMsg(), errorReporter).intValue();
        } else if (mode == Mode.LEFT) {
            Object end = qContext.pop().get();
            indexAble = qContext.pop().get();
            endInt = ValueUtils.assertType(end, Number.class, QLErrorCodes.INVALID_INDEX.name(),
                    QLErrorCodes.INVALID_INDEX.getErrorMsg(), errorReporter).intValue();
        } else if (mode == Mode.RIGHT) {
            Object start = qContext.pop().get();
            indexAble = qContext.pop().get();
            startInt = ValueUtils.assertType(start, Number.class, QLErrorCodes.INVALID_INDEX.name(),
                    QLErrorCodes.INVALID_INDEX.getErrorMsg(), errorReporter).intValue();
            endInt = indexAbleLen(indexAble);
        } else if (mode == Mode.COPY) {
            indexAble = qContext.pop().get();
            endInt = indexAbleLen(indexAble);
        }

        if (indexAble instanceof List) {
            List<?> result = listSlice((List<?>) indexAble, startInt, endInt);
            qContext.push(new DataValue(result));
            return QResult.NEXT_INSTRUCTION;
        } else if (indexAble != null && indexAble.getClass().isArray()) {
            Object result = arraySlice(indexAble, startInt, endInt);
            qContext.push(new DataValue(result));
            return QResult.NEXT_INSTRUCTION;
        } else {
            throw errorReporter.reportFormat("NONSUPPORT_INDEX", "%s not support index",
                    indexAble == null? "null": indexAble.getClass().getName());
        }
    }

    private int indexAbleLen(Object indexAble) {
        if (indexAble instanceof List) {
            return ((List<?>) indexAble).size();
        } else if (indexAble != null && indexAble.getClass().isArray()) {
            return Array.getLength(indexAble);
        } else {
            throw errorReporter.reportFormat("NONSUPPORT_INDEX", "%s not support index",
                    indexAble == null? "null": indexAble.getClass().getName());
        }
    }

    private List<?> listSlice(List<?> listObj, int originStart, int originEnd) {
        int start = Math.max(javaIndex(listObj.size(), originStart), 0);
        int end = Math.min(javaIndex(listObj.size(), originEnd), listObj.size());
        if (start >= end) {
            return new ArrayList<>(0);
        }

        return listObj.subList(start, end);
    }

    private Object arraySlice(Object arrObj, int originStart, int originEnd) {
        int arrLen = Array.getLength(arrObj);
        int start = Math.max(javaIndex(arrLen, originStart), 0);
        int end = Math.min(javaIndex(arrLen, originEnd), arrLen);
        if (start >= end) {
            return Array.newInstance(arrObj.getClass().getComponentType(), 0);
        }

        Object newArr = Array.newInstance(arrObj.getClass().getComponentType(), end - start);
        for (int i = start; i < end; i++) {
            Array.set(newArr, i - start, Array.get(arrObj, i));
        }
        return newArr;
    }

    private int javaIndex(int length, int qlIndex) {
        return qlIndex < 0? length + qlIndex: qlIndex;
    }

    @Override
    public int stackInput() {
        if (mode == Mode.BOTH) {
            return 2;
        } else if (mode == Mode.COPY) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": Slice", debug);
    }
}
