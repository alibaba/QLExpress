package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.RunEnvironment;

public class InstructionGoToWithNotNull extends Instruction {
    /**
     * 跳转指令的偏移量
     */
    private final int offset;
    private final boolean isPopStackData;

    public InstructionGoToWithNotNull(int aOffset, boolean aIsPopStackData) {
        this.offset = aOffset;
        this.isPopStackData = aIsPopStackData;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        Object o;
        if (!this.isPopStackData) {
            o = environment.peek().getObject(environment.getContext());
        } else {
            o = environment.pop().getObject(environment.getContext());
        }
        if (o != null) {
            if (environment.isTrace() && log.isDebugEnabled()) {
                log.debug("goto +" + this.offset);
            }
            environment.gotoWithOffset(this.offset);
        } else {
            if (environment.isTrace() && log.isDebugEnabled()) {
                log.debug("programPoint ++ ");
            }
            environment.programPointAddOne();
        }
    }

    @Override
    public String toString() {
        String result = "GoToIf[NOTNULL,isPop=" + this.isPopStackData + "] ";
        if (this.offset >= 0) {
            result = result + "+";
        }
        result = result + this.offset;
        return result;
    }
}
