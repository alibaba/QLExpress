package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.RunEnvironment;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.OperateDataCacheManager;

public class InstructionGoToWithCondition extends Instruction {
    /**
     * 跳转指令的偏移量
     */
    private int offset;
    private final boolean condition;
    private final boolean isPopStackData;

    public InstructionGoToWithCondition(boolean condition, int offset, boolean isPopStackData) {
        this.offset = offset;
        this.condition = condition;
        this.isPopStackData = isPopStackData;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        Object o;
        if (!this.isPopStackData) {
            o = environment.peek().getObject(environment.getContext());
            if (o == null) {
                environment.pop();
                environment.push(OperateDataCacheManager.fetchOperateData(false, boolean.class));
            }
        } else {
            o = environment.pop().getObject(environment.getContext());
        }
        boolean r;
        if (o == null) {
            r = false;
        } else if (o instanceof Boolean) {
            r = (Boolean)o;
        } else {
            throw new QLException(getExceptionPrefix() + "指令错误:" + o + " 不是Boolean");
        }
        if (r == this.condition) {
            environment.gotoWithOffset(this.offset);
        } else {
            environment.programPointAddOne();
        }
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        String result = "GoToIf[" + this.condition + ",isPop=" + this.isPopStackData + "] ";
        if (this.offset >= 0) {
            result = result + "+";
        }
        result = result + this.offset;
        return result;
    }
}
