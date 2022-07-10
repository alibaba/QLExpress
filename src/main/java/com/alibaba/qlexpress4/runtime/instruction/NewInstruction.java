package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.cache.QLConstructorCache;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.member.ConstructorHandler;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitConstructor;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.CacheUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.function.Supplier;

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
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        Parameters parameters = this.argNum == 0 ? null : qRuntime.pop(this.argNum);
        Class<?>[] paramTypes = new Class[this.argNum];
        Object[] objs = new Object[this.argNum];
        Object tmpObj;

        for (int i = 0; i < this.argNum; i++) {
            tmpObj = parameters.get(i).get();
            paramTypes[i] = tmpObj.getClass();
            objs[i] = tmpObj;
        }
        QLCaches qlCaches = qRuntime.getQLCaches();
        QLConstructorCache qlConstructorCache = qlCaches.getQlConstructorCache();
        QLImplicitConstructor cacheElement = CacheUtil.getConstructorCacheElement(qlConstructorCache, this.newClz, paramTypes);
        if (cacheElement == null) {
            cacheElement = ConstructorHandler.Preferred.findConstructorMostSpecificSignature(this.newClz, paramTypes);
            if (cacheElement==null || cacheElement.getConstructor() == null) {
                throw errorReporter.report("NEW_OBJECT_CREATE_ERROR", "not find constructor");
            }
            CacheUtil.setConstructorCacheElement(qlConstructorCache, this.newClz, paramTypes, cacheElement);
        }
        QLConvertResult convertResult = ParametersConversion.convert(objs, paramTypes, cacheElement.getConstructor().getParameterTypes(), cacheElement.needImplicitTrans());
        if (convertResult.getResultType().equals(QLConvertResultType.NOT_TRANS)) {
            throw errorReporter.report("NEW_OBJECT_CREATE_ERROR", "can not cast param");
        }
        Object constructorAccessible = getConstructorAccessible(cacheElement.getConstructor(),
                (Object[]) convertResult.getCastValue(), qlOptions.enableAllowAccessPrivateMethod());
        if(constructorAccessible == null){
            throw this.errorReporter.report("NEW_OBJECT_CREATE_ERROR", "can not create object");
        }
        Value dataInstruction = new DataValue(constructorAccessible);
        qRuntime.push(dataInstruction);
        return QResult.CONTINUE_RESULT;
    }


    private Object getConstructorAccessible(Constructor<?> constructor, Object[] params,
                                            boolean enableAllowAccessPrivateMethod){
        if(BasicUtil.isPublic(constructor)){
            return getConstructorSupplierAccessible(constructor, params).get();
        }else {
            if(enableAllowAccessPrivateMethod){
                getConstructorSupplierAccessible(constructor, params).get();
            }
        }
        return null;
    }


    private Supplier<Object> getConstructorSupplierAccessible(Constructor<?> constructor, Object[] params) {
        return () -> {
            try {
                return constructor.newInstance(params);
            } catch (Exception e) {
                return null;
            }
        };
    }

    private Supplier<Object> getConstructorSupplierNotAccessible(Constructor<?> constructor, Object[] params) {
        return () -> {
            try {
                synchronized (constructor) {
                    try {
                        constructor.setAccessible(true);
                        return constructor.newInstance(params);
                    } finally {
                        constructor.setAccessible(false);
                    }
                }
            } catch (Exception e) {
                return null;
            }
        };
    }

    @Override
    public int stackInput() {
        return argNum;
    }

    @Override
    public int stackOutput() {
        return 1;
    }
}
