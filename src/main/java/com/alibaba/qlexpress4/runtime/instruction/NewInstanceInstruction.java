package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersTypeConvertor;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Operation: new an object of specified class
 * Input: ${argNum} + 1
 * Output: 1
 * <p>
 * Author: DQinYuan
 */
public class NewInstanceInstruction extends QLInstruction {
    
    private final Class<?> newClz;
    
    private final int argNum;
    
    public NewInstanceInstruction(ErrorReporter errorReporter, Class<?> newClz, int argNum) {
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
            Value v = parameters.get(i);
            objs[i] = v.get();
            paramTypes[i] = v.getType();
        }
        ReflectLoader reflectLoader = qContext.getReflectLoader();
        Constructor<?> constructor = reflectLoader.loadConstructor(newClz, paramTypes);
        if (constructor == null) {
            throw errorReporter.reportFormat(QLErrorCodes.NO_SUITABLE_CONSTRUCTOR.name(),
                QLErrorCodes.NO_SUITABLE_CONSTRUCTOR.getErrorMsg(),
                Arrays.toString(paramTypes));
        }
        Object[] convertResult =
            ParametersTypeConvertor.cast(objs, constructor.getParameterTypes(), constructor.isVarArgs());
        Object newObject = newObject(constructor, convertResult);
        Value dataInstruction = new DataValue(newObject);
        qContext.push(dataInstruction);
        return QResult.NEXT_INSTRUCTION;
    }
    
    private Object newObject(Constructor<?> constructor, Object[] params) {
        try {
            return constructor.newInstance(params);
        }
        catch (InvocationTargetException e) {
            throw errorReporter.report(e.getTargetException(),
                QLErrorCodes.INVOKE_CONSTRUCTOR_INNER_ERROR.name(),
                QLErrorCodes.INVOKE_CONSTRUCTOR_INNER_ERROR.getErrorMsg());
        }
        catch (Exception e) {
            throw errorReporter.report(QLErrorCodes.INVOKE_CONSTRUCTOR_UNKNOWN_ERROR.name(),
                QLErrorCodes.INVOKE_CONSTRUCTOR_UNKNOWN_ERROR.getErrorMsg());
        }
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
        PrintlnUtils.printlnByCurDepth(depth,
            index + ": New instance of cls " + newClz.getSimpleName() + " with argNum " + argNum,
            debug);
    }
}
