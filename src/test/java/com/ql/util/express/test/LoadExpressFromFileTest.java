package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExportItem;
import com.ql.util.express.ExpressRunner;
import org.apache.commons.logging.Log;
import org.junit.Test;

public class LoadExpressFromFileTest {
    @Test
    public void testLoadFromFile() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        runner.loadExpress("functiondef");
        runner.loadExpress("main");
        ExportItem[] exports = runner.getExportInfo();
        for (ExportItem item : exports) {
            System.out.println(item.getGlobeName());
        }
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        Log log = new MyLog("玄难测试");
        Object r = runner.executeByExpressName("main", context, null,
            false, false, log);
        System.out.println("运行结果" + r);
        System.out.println("context:" + context);

        context = new DefaultContext<String, Object>();
        r = runner.execute("initial;累加;累加;return qh;",
            context, null, true, false, log);

        System.out.println("运行结果" + r);
        System.out.println("context:" + context);
    }

    @Test
    public void testLoadInclude() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.loadExpress("includeRoot");
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        Object r = runner.executeByExpressName("includeRoot", context, null, false, false, null);
        System.out.println(r);
        System.out.println(context);
    }
}
