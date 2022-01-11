package com.ql.util.express.instruction.opdata;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public class OperateDataKeyValue extends OperateData {
    private OperateData key;
    private OperateData value;

    public OperateDataKeyValue(OperateData key, OperateData value) {
        super(null, null);
        this.key = key;
        this.value = value;
    }

    public void initialDataKeyValue(OperateData key, OperateData value) {
        super.initial(null, null);
        this.key = key;
        this.value = value;
    }

    public void clearDataKeyValue() {
        super.clear();
        this.key = null;
        this.value = null;
    }

    public OperateData getKey() {
        return key;
    }

    public OperateData getValue() {
        return value;
    }

    @Override
    public Object getObjectInner(InstructionSetContext context) {
        throw new RuntimeException("没有实现方法：getObjectInner");
    }

    @Override
    public Class<?> getType(InstructionSetContext context) {
        throw new RuntimeException("没有实现方法：getType");
    }

    @Override
    public void setObject(InstructionSetContext parent, Object object) {
        throw new RuntimeException("没有实现方法：setObject");
    }

    @Override
    public String toString() {
        return this.key + ":" + this.value;
    }
}
