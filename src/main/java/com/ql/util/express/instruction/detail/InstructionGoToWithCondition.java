package com.ql.util.express.instruction.detail;

import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.OperateDataCacheManager;

import java.util.List;

public class InstructionGoToWithCondition extends Instruction {
    private static final long serialVersionUID = -4817805156872407837L;
    /**
     * 跳转指令的偏移量
     */
    int offset;
    boolean condition;
    boolean isPopStackData;

    public InstructionGoToWithCondition(boolean aCondition, int aOffset, boolean aIsPopStackData) {
        this.offset = aOffset;
        this.condition = aCondition;
        this.isPopStackData = aIsPopStackData;
    }

    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        Object o = null;
        if (this.isPopStackData == false) {
            o = environment.peek().getObject(environment.getContext());
            if (o == null) {
                environment.pop();
                environment.push(OperateDataCacheManager.fetchOperateData(false, boolean.class));
            }
        } else {
            o = environment.pop().getObject(environment.getContext());
        }
        boolean r = false;
        if (o == null) {
            r = false;
        } else if (o instanceof Boolean) {
            r = ((Boolean) o).booleanValue();
        } else {
            throw getException(getExceptionPrefix() + "指令错误:" + o + " 不是Boolean");
        }
        if (r == this.condition) {
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


    public String toString() {
        String result = "GoToIf[" + this.condition + ",isPop=" + this.isPopStackData + "] ";
        if (this.offset >= 0) {
            result = result + "+";
        }
        result = result + this.offset;
        return result;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isCondition() {
        return condition;
    }

    public void setCondition(boolean condition) {
        this.condition = condition;
    }

    public boolean isPopStackData() {
        return isPopStackData;
    }

    public void setPopStackData(boolean isPopStackData) {
        this.isPopStackData = isPopStackData;
    }


}
