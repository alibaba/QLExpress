package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

public class AClassDefineSingle {
    @Test
    public void testABC() throws Exception {
        String expressDefine =
            "class ABC(com.ql.util.express.test.BeanExample bean,String name){"
                + "整数值:bean.intValue;"
                + "};";
        String express =
            "ABC example = new ABC(new com.ql.util.express.test.BeanExample(),'xuannan');"
                + " example.整数值 =100 + 100;"
                + " print(example.整数值);"
                + "";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        runner.loadMutilExpress("", expressDefine);
        runner.loadMutilExpress("ClassTest", express);
        Object r = runner.executeByExpressName("ClassTest", context,
            null, true, false, null);

    }
}
