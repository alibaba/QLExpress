package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.data.DataField;
import com.alibaba.qlexpress4.utils.CacheUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @Operation: get specified field of object on the top of stack
 * @Input: 1
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class GetFieldInstruction extends QLInstruction {

    private final String fieldName;

    public GetFieldInstruction(ErrorReporter errorReporter, String fieldName) {
        super(errorReporter);
        this.fieldName = fieldName;
    }

    @Override
    public void execute(Parameters parameters, QRuntime qRuntime, QLOptions qlOptions) {
        Object bean = parameters.get(0).get();
        if(bean == null){
            throw errorReporter.report("GET_FIELD_INPUT_BEAN_NULL","input parameters is null");
        }

        Object fieldValue = null;
        DataField dataField = new DataField();
        try {
            fieldValue = CacheUtils.getFieldCacheElement(bean, this.fieldName, qlOptions.isAllowAccessPrivateMethod());
            dataField.set(fieldValue);
            qRuntime.push(dataField);
        } catch (InvocationTargetException e) {
            throw errorReporter.report("GET_FIELD_VALUE_ERROR","InvocationTargetException: "+e.getMessage());
        } catch (IllegalAccessException e) {
            throw errorReporter.report("GET_FIELD_VALUE_ERROR","IllegalAccessException: "+e.getMessage());
        } catch (Exception e){
            throw errorReporter.report("GET_FIELD_VALUE_ERROR","unExpected exception: "+e.getMessage());
        }

    }
}
