package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.CallResult;
import com.ql.util.express.ExpressLoader;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateDataArrayItem;
import com.ql.util.express.instruction.opdata.OperateDataAttr;
import com.ql.util.express.instruction.opdata.OperateDataField;
import com.ql.util.express.instruction.opdata.OperateDataKeyValue;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;

public class OperateDataCacheManager {
    private static final ThreadLocal<RunnerDataCache> RUNNER_DATA_CACHE_THREAD_LOCAL = ThreadLocal.withInitial(
        RunnerDataCache::new);

    private OperateDataCacheManager() {
        throw new IllegalStateException("Utility class");
    }

    public static void push(ExpressRunner expressRunner) {
        RUNNER_DATA_CACHE_THREAD_LOCAL.get().push(expressRunner);
    }

    public static IOperateDataCache getOperateDataCache() {
        return RUNNER_DATA_CACHE_THREAD_LOCAL.get().getCache();
    }

    public static OperateData fetchOperateData(Object obj, Class<?> type) {
        return getOperateDataCache().fetchOperateData(obj, type);
    }

    public static OperateDataAttr fetchOperateDataAttr(String name, Class<?> type) {
        return getOperateDataCache().fetchOperateDataAttr(name, type);
    }

    public static OperateDataLocalVar fetchOperateDataLocalVar(String name, Class<?> type) {
        return getOperateDataCache().fetchOperateDataLocalVar(name, type);
    }

    public static OperateDataField fetchOperateDataField(Object fieldObject, String fieldName) {
        return getOperateDataCache().fetchOperateDataField(fieldObject, fieldName);
    }

    public static OperateDataArrayItem fetchOperateDataArrayItem(OperateData arrayObject, int index) {
        return getOperateDataCache().fetchOperateDataArrayItem(arrayObject, index);
    }

    public static OperateDataKeyValue fetchOperateDataKeyValue(OperateData key, OperateData value) {
        return getOperateDataCache().fetchOperateDataKeyValue(key, value);
    }

    public static RunEnvironment fetRunEnvironment(InstructionSet instructionSet,
        InstructionSetContext instructionSetContext, boolean isTrace) {
        return getOperateDataCache().fetRunEnvironment(instructionSet, instructionSetContext, isTrace);
    }

    public static CallResult fetchCallResult(Object returnValue, boolean isExit) {
        return getOperateDataCache().fetchCallResult(returnValue, isExit);
    }

    public static InstructionSetContext fetchInstructionSetContext(boolean isExpandToParent,
        ExpressRunner expressRunner, IExpressContext<String, Object> parent, ExpressLoader expressLoader,
        boolean isSupportDynamicFieldName) {
        return getOperateDataCache().fetchInstructionSetContext(isExpandToParent, expressRunner, parent, expressLoader,
            isSupportDynamicFieldName);
    }

    public static long getFetchCount() {
        return getOperateDataCache().getFetchCount();
    }

    public static void resetCache() {
        getOperateDataCache().resetCache();
        RUNNER_DATA_CACHE_THREAD_LOCAL.get().pop();
    }
}

class RunnerDataCache {
    private IOperateDataCache cache;

    private final Stack<ExpressRunner> stack = new Stack<>();

    public void push(ExpressRunner expressRunner) {
        this.cache = expressRunner.getOperateDataCache();
        this.stack.push(expressRunner);
    }

    public void pop() {
        //原有的逻辑
        //this.cache = this.stack.pop().getOperateDataCache();

        //bugfix处理ExpressRunner嵌套情况下，cache还原的问题
        this.stack.pop();
        if (!this.stack.isEmpty()) {
            this.cache = this.stack.peek().getOperateDataCache();
        } else {
            this.cache = null;
        }
    }

    public IOperateDataCache getCache() {
        return cache;
    }
}