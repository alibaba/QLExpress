package com.ql.util.express.instruction.opdata;

import com.ql.util.express.InstructionSetContext;

public class OperateDataLocalVar extends OperateDataAttr {
    public OperateDataLocalVar(String name, Class<?> type) {
        super(name, type);
    }

    public void initialDataLocalVar(String name, Class<?> type) {
        super.initialDataAttr(name, type);
    }

    public void clearDataLocalVar() {
        super.clear();
    }

    @Override
    public Object getObjectInner(InstructionSetContext context) {
        try {
            return this.dataObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> getType(InstructionSetContext context) {
        return this.type;
    }

    @Override
    public void setObject(InstructionSetContext parent, Object value) {
        this.dataObject = value;
    }

    @Override
    public String toString() {
        try {
            return name + ":localVar";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
