package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.member.ConstructorHandler;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.CacheUtil;

import java.lang.reflect.Constructor;

/**
 * @Operation: new an object of specified class
 * @Input: ${argNum} + 1
 * @Output: 1
 * <p>
 * Author: DQinYuan
 */
public class NewInstruction extends QLInstruction {

    private final Class<?> newClz;

    private final int argNum;

    public NewInstruction(ErrorReporter errorReporter, Class<?> newClz, int argNum) {
        super(errorReporter);
        this.newClz = newClz;
        this.argNum = argNum;
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        Parameters parameters = qRuntime.pop(0);
        Class<?>[] paramTypes = new Class[this.argNum];
        Object[] objs = new Object[this.argNum];
        Object tmpObj;
        for (int i = 0; i < this.argNum; i++) {
            tmpObj = parameters.get(i).get();
            paramTypes[i] = tmpObj.getClass();
            objs[i] = tmpObj;
        }
        try {
            Constructor<?> cacheElement = CacheUtil.getConstructorCacheElement(this.newClz, paramTypes);
            if (cacheElement == null) {
                Constructor<?> constructor = ConstructorHandler.Preferred.findConstructorMostSpecificSignature(this.newClz, paramTypes);
                Value dataInstruction = new DataValue(constructor.newInstance(objs));
                qRuntime.push(dataInstruction);
                CacheUtil.setConstructorCacheElement(this.newClz, paramTypes, constructor);
            } else {
                Constructor<?> constructor = cacheElement;
                Value dataInstruction = new DataValue(constructor.newInstance(objs));
                qRuntime.push(dataInstruction);
            }
        } catch (Exception e) {
            throw this.errorReporter.report("NEW_OBJECT_CREATE_ERROR", "can not create object: constructor not find");
        }
    }
}
