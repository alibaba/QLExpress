package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.data.implicit.ConstructorReflect;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.Consumer;

/**
 * Operation: new an object of specified class
 * Input: ${argNum} + 1
 * Output: 1
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
        ReflectLoader reflectLoader = qContext.getReflectLoader();
        Optional<ConstructorReflect> constructorReflectOp = reflectLoader.loadConstructor(newClz, paramTypes);
        if (!constructorReflectOp.isPresent()) {
            throw errorReporter.reportFormat("CONSTRUCTOR_NOT_FOUND",
                    "constructor not found for types %s", Arrays.toString(paramTypes));
        }
        ConstructorReflect constructorReflect = constructorReflectOp.get();
        QLConvertResult convertResult = ParametersConversion.convert(objs, paramTypes,
                constructorReflect.getConstructor().getParameterTypes(),
                constructorReflect.needImplicitTrans(),constructorReflect.getVars());
        if (convertResult.getResultType().equals(QLConvertResultType.NOT_TRANS)) {
            throw errorReporter.reportFormat("CONSTRUCTOR_NOT_FOUND",
                    "constructor not found for types %s", Arrays.toString(paramTypes));
        }
        Object newObject = newObject(constructorReflect.getConstructor(), (Object[]) convertResult.getCastValue());
        Value dataInstruction = new DataValue(newObject);
        qContext.push(dataInstruction);
        return QResult.NEXT_INSTRUCTION;
    }

    private Object newObject(Constructor<?> constructor, Object[] params) {
        try {
            return constructor.newInstance(params);
        } catch (InvocationTargetException e) {
            throw errorReporter.report(e.getTargetException(), "CONSTRUCTOR_INNER_EXCEPTION",
                    "constructor inner exception");
        } catch (Exception e) {
            throw errorReporter.report("CONSTRUCTOR_UNKNOWN_EXCEPTION", "constructor unknown exception");
        }
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

    @Override
    public int stackInput() {
        return argNum;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": New cls " + newClz.getSimpleName()
                + " with argNum " + argNum, debug);
    }
}
