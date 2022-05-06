package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.member.FieldHandler;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataArray;
import com.alibaba.qlexpress4.runtime.data.DataClazz;
import com.alibaba.qlexpress4.runtime.data.DataField;
import com.alibaba.qlexpress4.runtime.data.DataMap;
import com.alibaba.qlexpress4.utils.BasicUtils;
import com.alibaba.qlexpress4.utils.CacheUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

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
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        Object bean = qRuntime.pop(0).get(0).get();
        if(bean == null){
            throw errorReporter.report("GET_FIELD_VALUE_ERROR","can not get field from null");
        }
        try {
            if (bean.getClass().isArray() && BasicUtils.LENGTH.equals(this.fieldName)) {
                Value dataArray = new DataArray((Object[]) bean);
                qRuntime.push(dataArray);
            } else if (bean instanceof Class) {
                if(BasicUtils.CLASS.equals(this.fieldName)) {
                    Value dataClazz = new DataClazz((Class<?>) bean);
                    qRuntime.push(dataClazz);
                }else {
                    getCacheFieldValue(qlOptions,(Class<?>) bean, bean, qRuntime);
                }
            } else if(bean instanceof Map){
                LeftValue dataMap = new DataMap((Map<?, ?>) bean, this.fieldName);
                qRuntime.push(dataMap);
            } else {
                getCacheFieldValue(qlOptions,bean.getClass(), bean, qRuntime);
            }
        }  catch (Exception e){
            throw errorReporter.report("GET_FIELD_VALUE_ERROR","can not get field: "+e.getMessage());
        }
    }

    /**
     * getValue from cache, if value not exist getInstance
     * @param qlOptions
     * @param clazz
     * @param bean
     * @param qRuntime
     */
    private void getCacheFieldValue(QLOptions qlOptions, Class<?> clazz, Object bean, QRuntime qRuntime){
        Object cacheElement = CacheUtils.getFieldCacheElement(clazz, this.fieldName);
        if(cacheElement == null){
            Method getMethod = MethodHandler.getGetter(clazz, this.fieldName, true);
            Method setMethod = MethodHandler.getSetter(clazz, this.fieldName);
            Field field = FieldHandler.Preferred.gatherFieldRecursive(clazz,this.fieldName);
            if(field == null){
                qRuntime.push(Value.NULL_VALUE);
            }else {
                LeftValue dataField = new DataField(field,getMethod,setMethod,clazz,bean,
                        this.fieldName,qlOptions.isAllowAccessPrivateMethod());
                qRuntime.push(dataField);
            }

        }else {
            qRuntime.push((LeftValue)cacheElement);
        }
    }
}
