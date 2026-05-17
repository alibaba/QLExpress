package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.IMethod;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.ReflectLoader;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.util.MethodInvokeUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Operation: Invoke specified method of each object in the list
 * Input: ${argNum} + 1
 * Output: 1, a list composed of return values from methods.
 * Author: DQinYuan
 */
public class SpreadMethodInvokeInstruction extends QLInstruction {
    
    private final String methodName;
    
    private final int argNum;
    
    public SpreadMethodInvokeInstruction(ErrorReporter errorReporter, String methodName, int argNum) {
        super(errorReporter);
        this.methodName = methodName;
        this.argNum = argNum;
    }
    
    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Parameters parameters = qContext.pop(this.argNum + 1);
        Object traversable = parameters.get(0).get();
        if (traversable == null) {
            if (qlOptions.isAvoidNullPointer()) {
                qContext.push(DataValue.NULL_VALUE);
                return QResult.NEXT_INSTRUCTION;
            }
            throw errorReporter.reportFormat(QLErrorCodes.NONTRAVERSABLE_OBJECT.name(),
                QLErrorCodes.NONTRAVERSABLE_OBJECT.getErrorMsg(),
                "null");
        }
        Class<?>[] type = new Class[this.argNum];
        Object[] params = new Object[this.argNum];
        for (int i = 0; i < this.argNum; i++) {
            Value v = parameters.get(i + 1);
            params[i] = v.get();
            type[i] = v.getType();
        }
        
        if (isTraversable(traversable)) {
            List<Object> result =
                spreadMethodInvokeRecursive(traversable, params, type, qContext.getReflectLoader(), qlOptions);
            qContext.push(new DataValue(result));
        }
        else {
            throw errorReporter.reportFormat(QLErrorCodes.NONTRAVERSABLE_OBJECT.name(),
                QLErrorCodes.NONTRAVERSABLE_OBJECT.getErrorMsg(),
                traversable.getClass().getName());
        }
        return QResult.NEXT_INSTRUCTION;
    }
    
    /**
     * Check if an object is traversable (Iterable or Array)
     */
    private boolean isTraversable(Object obj) {
        return obj instanceof Iterable || (obj != null && obj.getClass().isArray());
    }
    
    /**
     * Recursively flatten nested lists/arrays and invoke method on each element
     */
    private List<Object> spreadMethodInvokeRecursive(Object traversable, Object[] params, Class<?>[] type,
        ReflectLoader reflectLoader, QLOptions qlOptions) {
        List<Object> result = new ArrayList<>();
        
        if (traversable instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>)traversable;
            for (Object item : iterable) {
                processItem(item, result, params, type, reflectLoader, qlOptions);
            }
        }
        else if (traversable.getClass().isArray()) {
            int arrLen = Array.getLength(traversable);
            for (int i = 0; i < arrLen; i++) {
                Object item = Array.get(traversable, i);
                processItem(item, result, params, type, reflectLoader, qlOptions);
            }
        }
        
        return result;
    }
    
    /**
     * Process a single item: handle null, invoke method, or recursively flatten if nested
     */
    private void processItem(Object item, List<Object> result, Object[] params, Class<?>[] type,
        ReflectLoader reflectLoader, QLOptions qlOptions) {
        if (item == null) {
            if (qlOptions.isAvoidNullPointer()) {
                result.add(null);
                return;
            }
            throw errorReporter.report(new NullPointerException(),
                QLErrorCodes.NULL_METHOD_ACCESS.name(),
                QLErrorCodes.NULL_METHOD_ACCESS.getErrorMsg());
        }
        
        if (!isTraversable(item)) {
            // Leaf node - invoke method directly
            Value invokeRes =
                MethodInvokeUtils.findMethodAndInvoke(item, methodName, params, type, reflectLoader, errorReporter);
            result.add(invokeRes.get());
            return;
        }
        // If item itself is traversable, try to invoke method on it first
        IMethod method = reflectLoader.loadMethod(item, methodName, type);
        if (method != null) {
            Value invokeRes = MethodInvokeUtils.invokeIMethod(item, methodName, method, params, errorReporter);
            result.add(invokeRes.get());
            return;
        }
        // Then recursively flatten and invoke on nested elements
        List<Object> nestedResult = spreadMethodInvokeRecursive(item, params, type, reflectLoader, qlOptions);
        result.addAll(nestedResult);
    }
    
    @Override
    public int stackInput() {
        return argNum + 1;
    }
    
    @Override
    public int stackOutput() {
        return 1;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public int getArgNum() {
        return argNum;
    }
    
    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": SpreadMethodInvoke " + methodName, debug);
    }
}
