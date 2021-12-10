package com.ql.util.express.console;

public class ExampleDefine {
    private final String script;
    private final String context;

    public ExampleDefine(String aScript, String aContext) {
        this.script = aScript;
        this.context = aContext;
    }

    public String getScript() {
        return script;
    }

    public String getContext() {
        return context;
    }
}
