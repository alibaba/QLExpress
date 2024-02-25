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
        boolean isCatchException, boolean isSupportDynamicFieldName, long timeoutMills) throws Exception {
        try {
            OperateDataCacheManager.push(runner);
            return execute(runner, instructionSet, loader, iExpressContext, errorList, isTrace, isCatchException,
                    true, isSupportDynamicFieldName,
                    timeoutMills != -1?
                        // 优先使用参数传入
                        new ExecuteTimeOut(timeoutMills):
                        // 如果参数未传入, 则看一下是否有全局设置
                        QLExpressTimer.getTimeout() != -1?
                            new ExecuteTimeOut(QLExpressTimer.getTimeout()):
                            ExecuteTimeOut.NO_TIMEOUT);
        } finally {
            OperateDataCacheManager.resetCache();
        }
    }

    /**
     * 批量执行指令集合，指令集间可以共享 变量和函数
     *
     * @param runner 解释器
     * @param instructionSet 指令集
     * @param loader 加载器
     * @param iExpressContext 上下文
     * @param errorList 错误列表
     * @param isTrace 打印跟踪日志
     * @param isCatchException 捕获异常
     * @param isReturnLastData 返回最后一个数据
     * @param isSupportDynamicFieldName 日支持动态字段名
     * @param executeTimeOut  脚本运行的结束时限, -1 表示没有限制
     * @return
     * @throws Exception
     */
    public static Object execute(ExpressRunner runner, InstructionSet instructionSet, ExpressLoader loader,
        IExpressContext<String, Object> iExpressContext, List<String> errorList, boolean isTrace,
        boolean isCatchException, boolean isReturnLastData, boolean isSupportDynamicFieldName, ExecuteTimeOut executeTimeOut)
        throws Exception {
        InstructionSetContext context = OperateDataCacheManager.fetchInstructionSetContext(true, runner,
            iExpressContext, loader, isSupportDynamicFieldName);
        return execute(instructionSet, context, errorList, isTrace, isCatchException, isReturnLastData, executeTimeOut);
    }

    public static Object execute(InstructionSet set, InstructionSetContext context, List<String> errorList,
        boolean isTrace, boolean isCatchException, boolean isReturnLastData, ExecuteTimeOut executeTimeOut) throws Exception {
        RunEnvironment environment;
        Object result = null;
        environment = OperateDataCacheManager.fetRunEnvironment(set, context, isTrace, executeTimeOut);
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
