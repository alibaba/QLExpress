package com.ql.util.express.test.issue;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;

import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2022/12/30 下午8:08
 */
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
