package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Array;
import java.util.function.Consumer;

/**
 * new int[1][2][][]
 * @Operation: new array with multi dims
 * @Input: ${dims}
 * @Output: 1
 * Author: DQinYuan
 */
public class MultiNewArrayInstruction extends QLInstruction {

    private final Class<?> clz;

    private final int dims;

    public MultiNewArrayInstruction(ErrorReporter errorReporter, Class<?> clz, int dims) {
        super(errorReporter);
        this.clz = clz;
        this.dims = dims;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Parameters dimValues = qContext.pop(dims);
        int[] dimArray  = new int[dims];
        for (int i = 0; i < dims; i++) {
            Object dimValue = dimValues.get(i).get();
            if (!(dimValue instanceof Number)) {
                throw errorReporter.reportFormat("ARRAY_INVALID_SIZE_TYPE",
                        "array size must be number, %s dim is invalid", i);
            }
            dimArray[i] = ((Number) dimValue).intValue();
        }
        qContext.push(new DataValue(Array.newInstance(clz, dimArray)));
        return QResult.NEXT_INSTRUCTION;
    }

    @Override
    public int stackInput() {
        return dims;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "MultiNewArray with dims " + dims, debug);
    }
}
