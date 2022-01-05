package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.exception.QLException;

public class OperatorEvaluate extends OperatorBase {
    public OperatorEvaluate(String name) {
        this.name = name;
    }

    public OperatorEvaluate(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        return executeInner(parent, list.get(0), list.get(1));
    }

    public OperateData executeInner(InstructionSetContext parent,
        OperateData op1, OperateData op2) throws Exception {
        Class<?> targetType = op1.getDefineType();
        Class<?> sourceType = op2.getType(parent);
        if (targetType != null) {
            if (!ExpressUtil.isAssignable(targetType, sourceType)) {
                throw new QLException("赋值时候，类型转换错误：" + ExpressUtil.getClassName(sourceType) + " 不能转换为 "
                    + ExpressUtil.getClassName(targetType));
            }

        }
        Object result = op2.getObject(parent);
        if (targetType != null) {
            result = ExpressUtil.castObject(result, targetType, false);
        }
        op1.setObject(parent, result);
        return op1;
    }
}
