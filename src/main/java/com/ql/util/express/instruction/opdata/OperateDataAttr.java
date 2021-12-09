package com.ql.util.express.instruction.opdata;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public class OperateDataAttr extends OperateData {
    protected String name;

    public OperateDataAttr(String aName, Class<?> aType) {
        super(null, aType);
        this.name = aName;
    }

    public void initialDataAttr(String aName, Class<?> aType) {
        super.initial(null, aType);
        this.name = aName;
    }

    public void clearDataAttr() {
        super.clear();
        this.name = null;
    }

    public void setDefineType(Class<?> orgiType) {
        this.type = orgiType;
    }

    public Class<?> getDefineType() {
        return this.type;
    }

    public String getName() {
        return name;
    }

    public void toResource(StringBuilder builder, int level) {
        builder.append(this.name);
    }

    public String toString() {
        try {
            String str;
            if (this.type == null) {
                str = name;
            } else {
                str = name + "[" + ExpressUtil.getClassName(this.type) + "]";
            }
            return str;
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public Object getObjectInner(InstructionSetContext context) throws Exception {
        if (this.name.equalsIgnoreCase("null")) {
            return null;
        }
        if (context == null) {
            throw new RuntimeException("没有设置表达式计算的上下文，不能获取属性：\"" + this.name
                + "\"请检查表达式");
        }
        try {
            return context.get(this.name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getType(InstructionSetContext context) throws Exception {
        if (this.type != null) {
            return this.type;
        }
        Object obj = context.get(name);
        if (obj == null) {return null;} else {return obj.getClass();}
    }

    public void setObject(InstructionSetContext parent, Object object) throws Exception {
        try {
            parent.put(this.name, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
