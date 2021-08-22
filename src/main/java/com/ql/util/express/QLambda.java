package com.ql.util.express;

import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;
import org.apache.commons.logging.Log;

import java.util.List;

/**
 * 代表一个 lambda 表达式
 */
public class QLambda {

    private final InstructionSet functionSet;

    private final RunEnvironment environment;

    private final List<String> errorList;

    private final Log log;

    public QLambda(InstructionSet functionSet, RunEnvironment environment, List<String> errorList, Log log) {
        this.functionSet = functionSet;
        this.environment = environment;
        this.errorList = errorList;
        this.log = log;
    }

    public Object call(Object ... params) throws Exception {
        InstructionSetContext context = OperateDataCacheManager.fetchInstructionSetContext(
                true, environment.getContext().getExpressRunner(), environment.getContext(),
                environment.getContext().getExpressLoader(), environment.getContext().isSupportDynamicFieldName());
        OperateDataLocalVar[] vars = functionSet.getParameters();
        for (int i = 0; i < vars.length; i++) {
            OperateDataLocalVar var = OperateDataCacheManager.fetchOperateDataLocalVar(vars[i].getName(),
                    params.length <= i ? Object.class: params[i] == null? Object.class: params[i].getClass());
            context.addSymbol(var.getName(), var);
            var.setObject(context, params.length > i? params[i]: null);
        }

        return InstructionSetRunner.execute(functionSet, context, errorList, environment.isTrace(),
                false, true, log);
    }

    /**
     * 为了用起来更像 java 的 lambda 表达式而增加额外的一些方法
     */
    public void run() throws Exception {
        call();
    }

    /**
     * Consumer
     * BiConsumer
     */
    public void accept(Object ... params) throws Exception {
        call(params);
    }

    /**
     * Function
     * BiFunction
     */
    public Object apply(Object... params) throws Exception {
        return call(params);
    }

    /**
     * Supplier
     */
    public Object get() throws Exception {
        return call();
    }
}
