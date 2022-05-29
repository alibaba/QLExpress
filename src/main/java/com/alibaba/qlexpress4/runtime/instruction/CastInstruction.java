package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.BasicUtil;

/**
 * @Operation: force cast value to specified type
 * @Input: 2 targetCls and value
 * @Output: 1 casted value
 *
 * Author: DQinYuan
 */
public class CastInstruction extends QLInstruction {

    public CastInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        Object bean = qRuntime.pop().get();
        if (bean == null) {
            throw errorReporter.report("CAST_VALUE_ERROR", "can not get value from null");
        }
        try {
            Object value = BasicUtil.castObject(bean, this.targetClz, true);
            Value dataCast = new DataValue(value);
            qRuntime.push(dataCast);
        } catch (Exception e) {
            throw errorReporter.report("CAST_VALUE_ERROR", "can not cast value from this class:" + e.getMessage());
        }
    }
}
