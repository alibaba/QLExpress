package com.ql.util.express.instruction.opdata;

import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;

public class OperateClass extends OperateData {
    private final String name;
    private final Class<?> clazz;

    public OperateClass(String name, Class<?> clazz) {
        super(null, null);
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public void toResource(StringBuilder builder, int level) {
        builder.append(this.name);
    }

    @Override
    public Object getObjectInner(InstructionSetContext parent) {
        return clazz;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "Class:" + name;
    }
}
