package com.ql.util.express.instruction.opdata;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public class OperateClass extends OperateData {
    private String name;
    private Class<?> clazz;

    public OperateClass(String name, Class<?> aClass) {
        super(null, null);
        this.name = name;
        this.clazz = aClass;
    }

    @Override
    public void toResource(StringBuilder builder, int level) {
        builder.append(this.name);
    }

    @Override
    public String toString() {
        return "Class:" + name;
        // return name;
    }

    public Class<?> getVarClass() {
        return this.clazz;
    }

    public void reset(String aName, Class<?> newClass) {
        this.name = aName;
        this.clazz = newClass;
    }

    @Override
    public Object getObjectInner(InstructionSetContext parent) {
        return clazz;
    }

    public String getName() {
        return this.name;
    }
}
