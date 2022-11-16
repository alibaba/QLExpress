package com.ql.util.express.issue;

import java.util.Map;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

public class IssueBase {
    protected Object execute(String script, Map<String, Object> context) {
        ExpressRunner expressRunner = new ExpressRunner();
        IExpressContext<String, Object> iExpressContext = new DefaultContext<>();
        context.forEach(iExpressContext::put);
        try {
            return expressRunner.execute(script, iExpressContext, null, false, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
