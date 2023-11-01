package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
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
        Optional<Constructor<?>> constructorOp = reflectLoader.loadConstructor(newClz, paramTypes);
        if (!constructorOp.isPresent()) {
            throw errorReporter.reportFormat("CONSTRUCTOR_NOT_FOUND",
                    "constructor not found for types %s", Arrays.toString(paramTypes));
        }
        Constructor<?> constructor = constructorOp.get();
        Object[] convertResult = ParametersConversion.convert(
                objs, constructor.getParameterTypes(), constructor.isVarArgs()
        );
        Object newObject = newObject(constructor, convertResult);
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
