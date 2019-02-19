package com.ql.util.express.instruction.detail;

import com.ql.util.express.RunEnvironment;
import com.ql.util.express.exception.InstructionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;


public abstract class Instruction implements java.io.Serializable {

    private static final long serialVersionUID = 1361458333068300443L;
    protected static transient Log staticLog = LogFactory.getLog(Instruction.class);
    protected static transient Log log = staticLog;
    private Integer line = 0;

    public Instruction setLine(Integer line) {
        this.line = line;
        return this;
    }

    public Integer getLine() {
        return line;
    }

    public void setLog(Log aLog) {
        if (aLog != null) {
            this.log = aLog;
        }
    }

    public String getExceptionPrefix() {
        return "运行表达式出错，在第 " + line + " 行：";
    }

    public InstructionException getException(String massage, Throwable cause) {
        if (massage == null || massage.isEmpty()) {
            massage = getExceptionPrefix();
        }

        return new InstructionException(line, massage, cause);
    }

    public InstructionException getException(Throwable cause) {
        return getException(null, cause);
    }

    public InstructionException getException(String massage) {
        return getException(massage, null);
    }

    public abstract void execute(RunEnvironment environment, List<String> errorList) throws Exception;
}
