package com.ql.util.express.instruction.detail;

import java.util.List;

import com.ql.util.express.RunEnvironment;

public abstract class Instruction {
    private Integer line = 0;

    public Instruction setLine(Integer line) {
        this.line = line;
        return this;
    }

    public Integer getLine() {
        return line;
    }

    public String getExceptionPrefix() {
        return "run QlExpress Exception at line " + line + " :";
    }

    public abstract void execute(RunEnvironment environment, List<String> errorList) throws Exception;
}
