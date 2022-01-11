package com.ql.util.express.instruction.op;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;

public class OperatorAnonymousNewList extends OperatorBase {
    public OperatorAnonymousNewList(String name) {
        this.name = name;
    }

    public OperatorAnonymousNewList(String aliasName, String name, String errorInfo) {
        this.name = name;
        this.aliasName = aliasName;
        this.errorInfo = errorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            result.add(list.get(i).getObject(parent));
        }
        return OperateDataCacheManager.fetchOperateData(result, List.class);
    }
}
