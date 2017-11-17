package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

/**
 * Created by tianqiao on 17/11/13.
 */
public class MinusOperatorTest {
    
    @Test
    public void operatorReturn() throws Exception {
        ExpressRunner runner = new ExpressRunner(false,true);
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        String test1 = "return -50";
        System.out.println(runner.execute(test1, context, null, true, false));
    }
    
    
    @Test
    public void operatorThreeLogic() throws Exception {
        ExpressRunner runner = new ExpressRunner(false,true);
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        String test1 = "2>-1?-1:-2;";
        System.out.println(runner.execute(test1, context, null, true, false));
    }
    
}
