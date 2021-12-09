package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.RunEnvironment;

public class InstructionGoTo extends Instruction {
    private static final long serialVersionUID = 198094562177756098L;
    /**
     * 跳转指令的偏移量
     */
    int offset;
    public String name;

    public InstructionGoTo(int aOffset) {
        this.offset = aOffset;
    }

    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        if (environment.isTrace() && log.isDebugEnabled()) {
            log.debug(this);
        }
        environment.gotoWithOffset(this.offset);
    }

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

}
