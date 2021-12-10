package com.ql.util.express.instruction.opdata;

import com.ql.util.express.InstructionSetContext;

public class OperateDataAlias extends OperateDataAttr {
    OperateDataAttr realAttr;

    public OperateDataAlias(String aName, OperateDataAttr aRealAttr) {
        super(aName, null);
        this.realAttr = aRealAttr;
    }

    @Override
    public String toString() {
        try {
            return this.name + "[alias=" + this.realAttr.name + "]";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Override
    public Object getObjectInner(InstructionSetContext context) {
        try {
            return realAttr.getObject(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> getType(InstructionSetContext context) throws Exception {
        return realAttr.getType(context);
    }

    @Override
    public void setObject(InstructionSetContext context, Object object) {
        try {
            realAttr.setObject(context, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
