package com.ql.util.express;

import java.util.List;

import com.ql.util.express.config.QLExpressTimer;
import com.ql.util.express.instruction.OperateDataCacheManager;

public class InstructionSetRunner {

    private InstructionSetRunner() {
        throw new IllegalStateException("Utility class");
    }

    public static Object executeOuter(ExpressRunner runner, InstructionSet instructionSet, ExpressLoader loader,
        IExpressContext<String, Object> iExpressContext, List<String> errorList, boolean isTrace,
        boolean isCatchException, boolean isSupportDynamicFieldName) throws Exception {
        try {
            //开始计时
            QLExpressTimer.startTimer();

            OperateDataCacheManager.push(runner);
            return execute(runner, instructionSet, loader, iExpressContext, errorList, isTrace, isCatchException, true,
                isSupportDynamicFieldName);
        } finally {
            OperateDataCacheManager.resetCache();
        }
    }

    /**
     * 批量执行指令集合，指令集间可以共享 变量和函数
     *
     * @param runner
     * @param instructionSet
     * @param loader
     * @param iExpressContext
     * @param errorList
     * @param isTrace
     * @param isCatchException
     * @param isReturnLastData
     * @param isSupportDynamicFieldName
     * @return
     * @throws Exception
     */
    public static Object execute(ExpressRunner runner, InstructionSet instructionSet, ExpressLoader loader,
        IExpressContext<String, Object> iExpressContext, List<String> errorList, boolean isTrace,
        boolean isCatchException, boolean isReturnLastData, boolean isSupportDynamicFieldName)
        throws Exception {
        InstructionSetContext context = OperateDataCacheManager.fetchInstructionSetContext(true, runner,
            iExpressContext, loader, isSupportDynamicFieldName);
        return execute(instructionSet, context, errorList, isTrace, isCatchException, isReturnLastData);
    }

    public static Object execute(InstructionSet set, InstructionSetContext context, List<String> errorList,
        boolean isTrace, boolean isCatchException, boolean isReturnLastData) throws Exception {
        RunEnvironment environment;
        Object result = null;
        environment = OperateDataCacheManager.fetRunEnvironment(set, context, isTrace);
        try {
            CallResult tempResult = set.execute(environment, context, errorList, isReturnLastData);
            if (tempResult.isExit()) {
                result = tempResult.getReturnValue();
            }
        } catch (Exception e) {
            if (!isCatchException) {
                throw e;
            }
        }
        return result;
    }
}
