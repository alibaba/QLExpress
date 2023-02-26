package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.InstanceConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Array;
import java.util.function.Consumer;

/**
 * new int[] {1,2,3}
 * @Operation: new array with init items
 * @Input: ${length}
 * @Output: 1
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
        Object array = Array.newInstance(clz, length);
        Parameters initItems = qContext.pop(length);
        for (int i = 0; i < initItems.size(); i++) {
            QLConvertResult qlConvertResult = InstanceConversion.castObject(initItems.get(i).get(), clz);
            if (QLConvertResultType.NOT_TRANS == qlConvertResult.getResultType()) {
                throw errorReporter.reportFormat("INVALID_ARRAY_ITEM",
                        "item %s can not trans to array item type '%s'", i, clz.getName());
            }
            Array.set(array, i, qlConvertResult.getCastValue());
        }
        qContext.push(new DataValue(array));
        return QResult.NEXT_INSTRUCTION;
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
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "NewArray with length " + length, debug);
    }
}
