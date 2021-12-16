package com.ql.util.express;

import java.util.List;

import com.ql.util.express.config.QLExpressTimer;
import com.ql.util.express.instruction.OperateDataCacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InstructionSetRunner {
    private static final Log log = LogFactory.getLog(InstructionSetRunner.class);

    public static Object executeOuter(ExpressRunner runner, InstructionSet instructionSet, ExpressLoader loader,
        IExpressContext<String, Object> iExpressContext, List<String> errorList, boolean isTrace,
        boolean isCatchException, Log log, boolean isSupportDynamicFieldName) throws Exception {
        try {
            //开始计时
            QLExpressTimer.startTimer();

            OperateDataCacheManager.push(runner);
            return execute(runner, instructionSet, loader, iExpressContext, errorList, isTrace, isCatchException, true,
                log, isSupportDynamicFieldName);
        } finally {
            OperateDataCacheManager.resetCache(runner);
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
     * @param log
     * @param isSupportDynamicFieldName
     * @return
     * @throws Exception
     */
    public static Object execute(ExpressRunner runner, InstructionSet instructionSet, ExpressLoader loader,
        IExpressContext<String, Object> iExpressContext, List<String> errorList, boolean isTrace,
        boolean isCatchException, boolean isReturnLastData, Log log, boolean isSupportDynamicFieldName)
        throws Exception {
        InstructionSetContext context = OperateDataCacheManager.fetchInstructionSetContext(true, runner,
            iExpressContext, loader, isSupportDynamicFieldName);
        return execute(instructionSet, context, errorList, isTrace, isCatchException, isReturnLastData, log);
    }

    public static Object execute(InstructionSet set, InstructionSetContext context, List<String> errorList,
        boolean isTrace, boolean isCatchException, boolean isReturnLastData, Log log) throws Exception {
        RunEnvironment environment;
        Object result = null;
        environment = OperateDataCacheManager.fetRunEnvironment(set, context, isTrace);
        try {
            CallResult tempResult = set.execute(environment, context, errorList, isReturnLastData, log);
            if (tempResult.isExit()) {
                result = tempResult.getReturnValue();
            }
        } catch (Exception e) {
            if (isCatchException) {
                if (log != null) {
                    log.error(e.getMessage(), e);
                } else {
                    InstructionSetRunner.log.error(e.getMessage(), e);
                }
            } else {
                throw e;
            }
        }
        return result;
    }
}
