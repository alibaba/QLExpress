package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

/**
 * Created by tianqiao on 17/6/20.
 */
public class ArrayMapTest {
    @Test
    public void testMinus() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        //负数需要加括号来解决，就好比 1+(-1)
        String exp = "Map abc = NewMap(1:(-1),2:2); return abc.get(1) + abc.get(2)";
        IExpressContext<String, Object> context = new DefaultContext<>();
        Object result = runner.execute(exp, context, null, false, true);
        System.out.println(result);
    }
}
