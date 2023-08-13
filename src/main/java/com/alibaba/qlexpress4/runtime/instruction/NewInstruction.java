package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.cache.QLConstructorCache;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.member.ConstructorHandler;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitConstructor;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.CacheUtil;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;
import java.util.function.Consumer;

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
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Parameters parameters = this.argNum == 0 ? null : qContext.pop(this.argNum);
        Class<?>[] paramTypes = new Class[this.argNum];
        Object[] objs = new Object[this.argNum];

        for (int i = 0; i < this.argNum; i++) {
            Value v =  parameters.get(i);
            objs[i] = v.get();
            paramTypes[i] = v.getType();
        }
        QLCaches qlCaches = qContext.getQLCaches();
        QLConstructorCache qlConstructorCache = qlCaches.getQlConstructorCache();
        QLImplicitConstructor cacheElement = CacheUtil.getConstructorCacheElement(qlConstructorCache, this.newClz, paramTypes);
        if (cacheElement == null) {
            cacheElement = ConstructorHandler.Preferred.findConstructorMostSpecificSignature(this.newClz, paramTypes);
            if (cacheElement==null || cacheElement.getConstructor() == null) {
                throw errorReporter.report("NEW_OBJECT_CREATE_ERROR", "not find constructor");
            }
            CacheUtil.setConstructorCacheElement(qlConstructorCache, this.newClz, paramTypes, cacheElement);
        }
        QLConvertResult convertResult = ParametersConversion.convert(objs, paramTypes, cacheElement.getConstructor().getParameterTypes(),
                cacheElement.needImplicitTrans(),cacheElement.getVars());
        if (convertResult.getResultType().equals(QLConvertResultType.NOT_TRANS)) {
            throw errorReporter.report("NEW_OBJECT_CREATE_ERROR", "can not cast param");
        }
        Object constructorAccessible = getConstructorAccessible(cacheElement.getConstructor(),
                (Object[]) convertResult.getCastValue(), qlOptions.allowAccessPrivateMethod());
        if(constructorAccessible == null){
            throw this.errorReporter.report("NEW_OBJECT_CREATE_ERROR", "can not create object");
        }
        Value dataInstruction = new DataValue(constructorAccessible);
        qContext.push(dataInstruction);
        return QResult.NEXT_INSTRUCTION;
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

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "New cls " + newClz.getSimpleName()
                + " with argNum " + argNum, debug);
    }
}
