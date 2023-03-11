package com.alibaba.qlexpress4.test.issue;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
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
        Express4Runner expressRunner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> iExpressContext = new DefaultContext<>();
        context.forEach(iExpressContext::put);
        try {
            return expressRunner.execute(script, iExpressContext, QLOptions.DEFAULT_OPTIONS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
