package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ObjTypeConvertor;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Array;
import java.util.function.Consumer;

/**
 * new int[] {1,2,3}
 * Operation: new array with init items
 * Input: ${length}
 * Output: 1
 * Author: DQinYuan
 */
public class NewArrayInstruction extends QLInstruction {

    private final Class<?> clz;

    private final int length;

    public NewArrayInstruction(ErrorReporter errorReporter, Class<?> clz, int length) {
        super(errorReporter);
        this.clz = clz;
        this.length = length;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        if (!qlOptions.checkArrLen(length)) {
            throw errorReporter.reportFormat("EXCEED_MAX_ARR_LENGTH",
                    "new array length %d, exceed max allowed length %d", length, qlOptions.getMaxArrLength());
        }
        Object array = Array.newInstance(clz, length);
        Parameters initItems = qContext.pop(length);
        for (int i = 0; i < initItems.size(); i++) {
            Object initItemObj = initItems.get(i).get();
            ObjTypeConvertor.QConverted qlConvertResult = ObjTypeConvertor.cast(initItemObj, clz);
            if (!qlConvertResult.isConvertible()) {
                throw errorReporter.reportFormat("INCOMPATIBLE_ARRAY_ITEM_TYPE",
                        "item %s with type %s incompatible with array type %s", i,
                        initItemObj == null? "null": initItemObj.getClass().getName(), clz.getName());
            }
            Array.set(array, i, qlConvertResult.getConverted());
        }
        qContext.push(new DataValue(array));
        return QResult.NEXT_INSTRUCTION;
    }

    public Class<?> getClz() {
        return clz;
    }

    @Override
    public int stackInput() {
        return length;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": NewArray with length " + length, debug);
    }
}
