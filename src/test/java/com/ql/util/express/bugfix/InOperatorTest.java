package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.instruction.op.OperatorIn;
import org.junit.Test;

/**
 * Created by tianqiao on 17/6/15.
 */
public class InOperatorTest {

    @Test
    public void testAllByFunction() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addOperator("widelyin", new OperatorIn("widelyin") {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                if (list[0] == null) {
                    return false;
                }
                return super.executeInner(list);
            }
        });
        //注意可以使用 data in (2,3,4) ，但无法使用data widelyin (2,3,4)，因为默认addOperator的是二元操作符，
        // com.ql.util.express.instruction.InInstructionFactory对语法树做了特殊处理
        String exp = "data widelyin [2,3,4]";
        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("data", 2);
        Object result = runner.execute(exp, context, null, false, true);
        System.out.println(result);
    }
}
