package com.ql.util.express.instruction.detail;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.RunEnvironment;


public abstract class Instruction implements java.io.Serializable {
	
	private static final long serialVersionUID = 1361458333068300443L;
	protected static transient  Log staticLog = LogFactory.getLog(Instruction.class);
	protected static transient Log log = staticLog;
	private Integer line = 0;
	public Instruction setLine(Integer line)
    {
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
	
	public String getExceptionPrefix()
    {
        return "run QlExpress Exception at line "+line+" :";
    }
	public abstract void execute(RunEnvironment environment, List<String> errorList)
			throws Exception;
}
