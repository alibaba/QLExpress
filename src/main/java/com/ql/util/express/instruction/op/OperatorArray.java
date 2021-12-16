package com.ql.util.express.instruction.op;

import java.util.List;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.OperateDataCacheManager;

public class OperatorArray extends OperatorBase {
    public OperatorArray(String name) {
        this.name = name;
    }

    public OperatorArray(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        OperateData firstOperateData = list.get(0);
        if (firstOperateData == null || firstOperateData.getObject(parent) == null) {
            throw new QLException("对象为null，不能执行数组相关操作");
        }

        Object tmpObject = firstOperateData.getObject(parent);

        if (!tmpObject.getClass().isArray()) {
            Object property = list.get(1).getObject(parent);
            //支持data.get(index) ->data[index]
            if (tmpObject instanceof List && property instanceof Number) {
                int index = ((Number)property).intValue();
                return OperateDataCacheManager.fetchOperateDataArrayItem(firstOperateData, index);
            }
            //支持data.code -> data['code']
            if (property instanceof String || property instanceof Character) {
                return OperateDataCacheManager.fetchOperateDataField(tmpObject, String.valueOf(property));
            }
        }
        //支持原生Array：data[index]
        int index = ((Number)list.get(1).getObject(parent)).intValue();
        return OperateDataCacheManager.fetchOperateDataArrayItem(firstOperateData, index);
    }
}
