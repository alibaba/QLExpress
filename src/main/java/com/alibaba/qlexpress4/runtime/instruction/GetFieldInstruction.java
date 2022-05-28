package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.member.FieldHandler;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.*;
import com.alibaba.qlexpress4.runtime.data.cache.CacheFieldValue;
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
        Object bean = qRuntime.pop().get();
        if(bean == null){
            throw errorReporter.report("GET_FIELD_VALUE_ERROR","can not get field from null");
        }
        try {
            if (bean.getClass().isArray() && BasicUtils.LENGTH.equals(this.fieldName)) {
                Value dataArray = new DataValue(((Object[]) bean).length);
                qRuntime.push(dataArray);
            } else if (bean instanceof Class) {
                if(BasicUtils.CLASS.equals(this.fieldName)) {
                    Value dataClazz = new DataValue(bean);
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
            LeftValue dataField = new DataField(field,getMethod,setMethod,clazz,bean,
                    this.fieldName,qlOptions.enableAllowAccessPrivateMethod());
            qRuntime.push(dataField);
            if(cacheElement!=null){
                CacheFieldValue cacheFieldValue = new CacheFieldValue(getMethod,setMethod,field);
                CacheUtils.setFieldCacheElement(clazz, this.fieldName, cacheFieldValue);
            }
        }else {
            CacheFieldValue cacheFieldValue = (CacheFieldValue) cacheElement;
            LeftValue dataField = new DataField(cacheFieldValue.getField(),cacheFieldValue.getGetMethod(),
                    cacheFieldValue.getSetMethod(),clazz,bean,
                    this.fieldName,qlOptions.enableAllowAccessPrivateMethod());
            qRuntime.push(dataField);
        }
    }
}
