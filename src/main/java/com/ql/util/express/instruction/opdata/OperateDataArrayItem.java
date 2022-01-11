package com.ql.util.express.instruction.opdata;

import java.lang.reflect.Array;
import java.util.List;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public class OperateDataArrayItem extends OperateDataAttr {
    private OperateData arrayObject;
    private int index;

    public OperateDataArrayItem(OperateData arrayObject, int index) {
        super("array[" + arrayObject + "," + index + "]", null);
        this.arrayObject = arrayObject;
        this.index = index;
    }

    public void initialDataArrayItem(OperateData arrayObject, int index) {
        super.initialDataAttr("array[" + arrayObject + "," + index + "]", null);
        this.arrayObject = arrayObject;
        this.index = index;
    }

    public void clearDataArrayItem() {
        super.clearDataAttr();
        this.arrayObject = null;
        this.index = -1;
    }

    @Override
    public void toResource(StringBuilder builder, int level) {
        builder.append(this.index);
    }

    @Override
    public Class<?> getType(InstructionSetContext context) throws Exception {
        return this.arrayObject.getObject(context).getClass().getComponentType();
    }

    @Override
    public Object getObjectInner(InstructionSetContext context) {
        try {
            if (this.arrayObject.getObject(context) instanceof List) {
                return ((List)this.arrayObject.getObject(context)).get(this.index);
            } else {
                return Array.get(this.arrayObject.getObject(context), this.index);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setObject(InstructionSetContext context, Object value) {
        try {
            if (this.arrayObject.getObject(context) instanceof List) {
                ((List)this.arrayObject.getObject(context)).set(this.index, value);
            } else {
                Array.set(this.arrayObject.getObject(context), this.index, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}







