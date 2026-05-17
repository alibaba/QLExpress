package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Operation: get field of each object in the list
 * Input: 1
 * Output: 1, a list composed of field values
 * Author: DQinYuan
 */
public class SpreadGetFieldInstruction extends QLInstruction {
    
    private static final String KEY = "key";
    
    private static final String VALUE = "value";
    
    private final String fieldName;
    
    public SpreadGetFieldInstruction(ErrorReporter errorReporter, String fieldName) {
        super(errorReporter);
        this.fieldName = fieldName;
    }
    
    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Object traversable = qContext.pop().get();
        if (traversable == null) {
            if (qlOptions.isAvoidNullPointer()) {
                qContext.push(DataValue.NULL_VALUE);
                return QResult.NEXT_INSTRUCTION;
            }
            throw errorReporter.reportFormat(QLErrorCodes.NONTRAVERSABLE_OBJECT.name(),
                QLErrorCodes.NONTRAVERSABLE_OBJECT.getErrorMsg(),
                "null");
        }
        
        // Map special handling for key/value access
        if (traversable instanceof Map) {
            Map<?, ?> lhm = (Map<?, ?>)traversable;
            List<Object> result = new ArrayList<>();
            for (Map.Entry<?, ?> entry : lhm.entrySet()) {
                if (KEY.equals(fieldName)) {
                    result.add(entry.getKey());
                }
                else if (VALUE.equals(fieldName)) {
                    result.add(entry.getValue());
                }
                else {
                    throw errorReporter.reportFormat(QLErrorCodes.FIELD_NOT_FOUND.name(),
                        QLErrorCodes.FIELD_NOT_FOUND.getErrorMsg(),
                        fieldName);
                }
            }
            qContext.push(new DataValue(result));
        }
        else if (isTraversable(traversable)) {
            List<Object> result = spreadGetFieldRecursive(traversable, qContext, qlOptions);
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
     * Recursively flatten nested lists/arrays and get field from each element
     */
    private List<Object> spreadGetFieldRecursive(Object traversable, QContext qContext, QLOptions qlOptions) {
        List<Object> result = new ArrayList<>();
        
        if (traversable instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>)traversable;
            for (Object item : iterable) {
                processItem(item, result, qContext, qlOptions);
            }
        }
        else if (traversable.getClass().isArray()) {
            int arrLen = Array.getLength(traversable);
            for (int i = 0; i < arrLen; i++) {
                Object item = Array.get(traversable, i);
                processItem(item, result, qContext, qlOptions);
            }
        }
        
        return result;
    }
    
    /**
     * Process a single item: handle null, get field, or recursively flatten if nested
     */
    private void processItem(Object item, List<Object> result, QContext qContext, QLOptions qlOptions) {
        if (item == null) {
            if (qlOptions.isAvoidNullPointer()) {
                result.add(null);
                return;
            }
            throw errorReporter.report(new NullPointerException(),
                QLErrorCodes.NULL_FIELD_ACCESS.name(),
                QLErrorCodes.NULL_FIELD_ACCESS.getErrorMsg());
        }
        
        // Check if the field exists at current level
        Value fieldValue = qContext.getReflectLoader().loadField(item, fieldName, false, errorReporter);
        if (fieldValue == null) {
            // Field not found, check if item is nested list/array
            if (isTraversable(item)) {
                // Recursively flatten
                result.addAll(spreadGetFieldRecursive(item, qContext, qlOptions));
            }
            else {
                throw errorReporter.reportFormat(QLErrorCodes.FIELD_NOT_FOUND.name(),
                    QLErrorCodes.FIELD_NOT_FOUND.getErrorMsg(),
                    fieldName);
            }
        }
        else {
            result.add(fieldValue.get());
        }
    }
    
    @Override
    public int stackInput() {
        return 1;
    }
    
    @Override
    public int stackOutput() {
        return 1;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": SpreadGetField " + fieldName, debug);
    }
}
