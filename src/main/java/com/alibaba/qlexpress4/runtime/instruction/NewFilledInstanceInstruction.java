package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Operation: new a Map with top ${keys.length} stack element
 * Input: ${keys.length}
 * Output: 1
 * <p>
 * Author: DQinYuan
 */
public class NewFilledInstanceInstruction extends QLInstruction {
    
    private final Class<?> newCls;
    
    private final List<String> keys;
    
    public NewFilledInstanceInstruction(ErrorReporter errorReporter, Class<?> newCls, List<String> keys) {
        super(errorReporter);
        this.newCls = newCls;
        this.keys = keys;
    }
    
    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Object instance = newInstance(qContext);
        Parameters initItems = qContext.pop(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            Object initValue = initItems.get(i).get();
            String fieldName = keys.get(i);
            Value fieldValue = qContext.getReflectLoader().loadField(instance, fieldName, false, errorReporter);
            if (fieldValue == null) {
                // ignore field that don't exist
                continue;
            }
            if (!(fieldValue instanceof LeftValue)) {
                throw errorReporter.reportFormat(QLErrorCodes.INVALID_ASSIGNMENT.name(),
                    QLErrorCodes.INVALID_ASSIGNMENT.getErrorMsg(),
                    "of field '" + fieldName + "'");
            }
            ((LeftValue)fieldValue).set(initValue, errorReporter);
        }
        qContext.push(new DataValue(instance));
        return QResult.NEXT_INSTRUCTION;
    }
    
    private Object newInstance(QContext qContext) {
        Constructor<?> constructor = qContext.getReflectLoader().loadConstructor(newCls, new Class[0]);
        try {
            return constructor.newInstance();
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
        return keys.size();
    }
    
    @Override
    public int stackOutput() {
        return 1;
    }
    
    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth,
            index + ": New instace of cls " + newCls.getSimpleName() + " with fields " + keys,
            debug);
    }
}
