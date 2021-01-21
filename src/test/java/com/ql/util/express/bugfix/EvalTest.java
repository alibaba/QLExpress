package com.ql.util.express.bugfix;

import com.ql.util.express.*;
import com.ql.util.express.instruction.op.OperatorBase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 测试引擎嵌套执行
 */
public class EvalTest {

    @Test
    public void evalTest() throws Exception {
        String evalExpress = "eval('eval(\\'1+1\\')')";
        final ExpressRunner runner = new ExpressRunner(false,true);
        runner.addFunction("eval", new OperatorBase() {
            @Override
            public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
                Object res = runner.eval(list.get(0).toString(), parent,
                        null, true);
                return new OperateData(res, res.getClass());
            }
        });

        Object res = runner.execute(evalExpress, new DefaultContext<String, Object>(), null,
                false, true);
        assertEquals(2, res);
    }

    @Test
    public void evalUserVariableTest() throws Exception {
        String evalExpress = "m = 'aaaa';" +
                "eval('m')";
        final ExpressRunner runner = new ExpressRunner(false,true);
        runner.addFunction("eval", new OperatorBase() {
            @Override
            public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
                Object res = runner.eval(list.get(0).toString(), parent,
                        null, true);
                return new OperateData(res, res.getClass());
            }
        });

        Object res = runner.execute(evalExpress, new DefaultContext<String, Object>(), null,
                false, true);
        assertEquals("aaaa", res);
    }

}
