package com.ql.util.express.rule;

import java.util.Map;

/**
 * Created by tianqiao on 16/12/12.
 */
public class RuleResult {

    private boolean hasException = false;
    private Rule rule;
    private Map<String,Boolean> traceMap;
    private Object result;
    private String script;
    
    public String getScript() {
        return script;
    }
    
    public void setScript(String script) {
        this.script = script;
    }
    
    public boolean isHasException() {
        return hasException;
    }
    
    public void setHasException(boolean hasException) {
        this.hasException = hasException;
    }
    
    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public Map<String, Boolean> getTraceMap() {
        return traceMap;
    }

    public void setTraceMap(Map<String, Boolean> traceMap) {
        this.traceMap = traceMap;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
