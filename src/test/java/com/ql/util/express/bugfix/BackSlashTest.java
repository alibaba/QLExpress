package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

//脚本中包含 反斜杠 测试
public class BackSlashTest {

    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        String exp = "a=\"\\\\\";";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        Object result = runner.execute(exp,context,null,false,true);
        System.out.println(result);
    }
}
