package com.ql.util.express.cfuture;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.op.OperatorBase;
import org.junit.Test;

public class DebugQlScript {

    @Test
    public void testPrintContext() throws Exception {

        ExpressRunner runner = new ExpressRunner();
        runner.addFunction("printContext", new OperatorBase() {
            @Override
            public OperateData executeInner(InstructionSetContext parent, ArraySwap list) {
                System.out.println(parent.getParent());//打印对象
                return new OperateData(parent.getParent(), parent.getParent().getClass());
            }
        });

        String[][] testList = new String[][] {
            new String[] {"a=10;b=20;printContext();a=20;c=30;printContext();", null},
        };

        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("name", "test");
        for (String[] test : testList) {
            Object r = runner.execute(test[0], context, null, true, false);
            System.out.println(r);
        }
    }
}
