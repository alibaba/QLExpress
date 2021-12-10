package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;
import org.junit.Test;

/**
 * Created by tianqiao on 17/3/2.
 */
public class RecursivelyTest {

    static final ExpressRunner runner = new ExpressRunner();
    static final ExpressRunner runnerInner = new ExpressRunner();

    static {

        Operator exeOperator = new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                System.out.println("executeInner:r_exeAll");
                IExpressContext<String, Object> context = new DefaultContext<>();
                runnerInner.execute("1+2", context, null, false, true);
                System.out.println(list[0]);
                return null;
            }
        };

        runner.addFunction("r_exeAll", exeOperator);
        runnerInner.addFunction("r_exeAll", exeOperator);
    }

    @Test
    public void testAllByFunction() throws Exception {

        String exp = "r_exeAll(1,2,3)";
        IExpressContext<String, Object> context = new DefaultContext<>();
        runner.execute(exp, context, null, false, true);
    }
}
