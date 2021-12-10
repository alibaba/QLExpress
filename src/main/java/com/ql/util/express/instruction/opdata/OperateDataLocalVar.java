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

    public String toString() {
        try {
            return name + ":localVar";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public Object getObjectInner(InstructionSetContext context) {
        try {
            return this.dataObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getType(InstructionSetContext context) {
        return this.type;
    }

    public void setObject(InstructionSetContext parent, Object value) {
        this.dataObject = value;
    }
}
