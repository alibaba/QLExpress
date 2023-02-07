package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.RunEnvironment;

public class InstructionGoTo extends Instruction {
    /**
     * 跳转指令的偏移量
     */
    private int offset;
    private String name;

    public InstructionGoTo(int offset) {
        this.offset = offset;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) {
        environment.gotoWithOffset(this.offset);
    }

    @Override
    public String toString() {
        String result = (this.name == null ? "" : this.name + ":") + "GoTo ";
        if (this.offset >= 0) {
            result = result + "+";
        }
        result = result + this.offset + " ";
        return result;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
