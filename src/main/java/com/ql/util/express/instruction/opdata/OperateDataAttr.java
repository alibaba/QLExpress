package com.ql.util.express.instruction.opdata;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public class OperateDataAttr extends OperateData {
    protected String name;

    public OperateDataAttr(String name, Class<?> type) {
        super(null, type);
        this.name = name;
    }

    public void initialDataAttr(String name, Class<?> type) {
        super.initial(null, type);
        this.name = name;
    }

    public void clearDataAttr() {
        super.clear();
        this.name = null;
    }

    public void setDefineType(Class<?> originalType) {
        this.type = originalType;
    }

    @Override
    public Class<?> getDefineType() {
        return this.type;
    }

    public String getName() {
        return name;
    }

    @Override
    public void toResource(StringBuilder builder, int level) {
        builder.append(this.name);
    }

    @Override
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

    @Override
    public Object getObjectInner(InstructionSetContext context) throws Exception {
        if ("null".equalsIgnoreCase(this.name)) {
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

    @Override
    public Class<?> getType(InstructionSetContext context) throws Exception {
        if (this.type != null) {
            return this.type;
        }
        Object obj = context.get(name);
        if (obj == null) {
            return null;
        } else {
            return obj.getClass();
        }
    }

    @Override
    public void setObject(InstructionSetContext parent, Object object) throws Exception {
        try {
            parent.put(this.name, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
