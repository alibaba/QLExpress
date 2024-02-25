package com.ql.util.express.bugfix;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.exception.QLTimeoutException;
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
                Object res = runner.execute(list.get(0).toString(), parent, null, false, true);
                return new OperateData(res, res.getClass());
            }
        });

        Object res = runner.execute(express, new DefaultContext<>(), null, false, true);
        Assert.assertEquals(2, res);

        Object res1 = runner.execute("m = 'aaa'; eval('m')", new DefaultContext<>(), null, false, true);
        Assert.assertEquals("aaa", res1);
    }

    @Test
    public void executeReentrantTimerTest() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addFunction("eval", new OperatorBase() {
            @Override
            public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
                Thread.sleep(2000);
                Object result = runner.execute(list.get(0).toString(), parent, null,
                        false, true, ((Number) list.get(1).getObject(parent)).longValue());
                return new OperateData(result, result.getClass());
            }
        });

        Object result = runner.execute("a=eval('a=eval(\\'a=1+1\\',10)', 2200)", new DefaultContext<>(),
                null, false, true, 4100);
        Assert.assertEquals(2, result);

        try {
            runner.execute("a=eval('a=eval(\\'a=1+1\\', 10)',1000)",
                    new DefaultContext<>(), null, false, true, 4100);
            Assert.fail();
        } catch (QLException e) {
            Assert.assertTrue(e.getCause() instanceof QLTimeoutException);
        } catch (Throwable e) {
            Assert.fail();
        }
    }
}
