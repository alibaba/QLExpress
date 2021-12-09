package com.ql.util.express.bugfix;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.op.OperatorBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * 线程可重入执行测试
 */
public class ExecuteReentrantTest {

    @Test
    public void executeReentrantTest() throws Exception {
        String express = "eval('eval(\\'1+1\\')')";
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addFunction("eval", new OperatorBase() {
            @Override
            public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
                Object res = runner.execute(list.get(0).toString(), parent, null,
                    false, true);
                return new OperateData(res, res.getClass());
            }
        });

        Object res = runner.execute(express, new DefaultContext<>(), null,
            false, true);
        Assert.assertEquals(2, res);

        Object res1 = runner.execute("m = 'aaa';" +
                "eval('m')", new DefaultContext<>(), null,
            false, true);
        Assert.assertEquals("aaa", res1);
    }
}
