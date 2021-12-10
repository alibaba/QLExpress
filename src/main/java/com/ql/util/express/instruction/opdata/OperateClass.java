package com.ql.util.express.instruction.opdata;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public class OperateClass extends OperateData {
    private String name;
    private Class<?> m_class;

    public OperateClass(String name, Class<?> aClass) {
        super(null, null);
        this.name = name;
        this.m_class = aClass;
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
        return this.m_class;
    }

    public void reset(String aName, Class<?> newClass) {
        this.name = aName;
        this.m_class = newClass;
    }

    @Override
    public Object getObjectInner(InstructionSetContext parent) {
        return m_class;
    }

    public String getName() {
        return this.name;
    }
}
