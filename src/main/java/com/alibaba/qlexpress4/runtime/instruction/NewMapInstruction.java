package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Operation: new a Map with top ${keys.length} stack element
 * @Input: ${keys.length}
 * @Output: 1
 * <p>
 * Author: DQinYuan
 */
public class NewMapInstruction extends QLInstruction {

    private final List<String> keys;

    public NewMapInstruction(ErrorReporter errorReporter, List<String> keys) {
        super(errorReporter);
        this.keys = keys;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Parameters initItems = qContext.pop(keys.size());
        Map<String, Object> m = new LinkedHashMap<>();
        // TODO: 遍历逻辑优化
        for (int i = 0; i < keys.size(); i++) {
            m.put(keys.get(i), initItems.get(i).get());
        }
        qContext.push(new DataValue(m));
        return QResult.CONTINUE_RESULT;
    }

    @Override
    public int stackInput() {
        return keys.size();
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "NewMap by keys:" + keys, debug);
    }
}
