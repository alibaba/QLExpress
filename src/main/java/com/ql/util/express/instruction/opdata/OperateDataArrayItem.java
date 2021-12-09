package com.ql.util.express.instruction.opdata;

import java.lang.reflect.Array;
import java.util.List;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public class OperateDataArrayItem extends OperateDataAttr {
    OperateData arrayObject;
    int index;

    public OperateDataArrayItem(OperateData aArrayObject, int aIndex) {
        super("array[" + aArrayObject + "," + aIndex + "]", null);
        this.arrayObject = aArrayObject;
        this.index = aIndex;
    }

    public void initialDataArrayItem(OperateData aArrayObject, int aIndex) {
        super.initialDataAttr("array[" + aArrayObject + "," + aIndex + "]", null);
        this.arrayObject = aArrayObject;
        this.index = aIndex;
    }

    public void clearDataArrayItem() {
        super.clearDataAttr();
        this.arrayObject = null;
        this.index = -1;
    }

    public void toResource(StringBuilder builder, int level) {
        builder.append(this.index);
    }

    public Class<?> getType(InstructionSetContext context) throws Exception {
        return this.arrayObject.getObject(context).getClass().getComponentType();
    }

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







