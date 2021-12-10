package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

/**
 * Created by tianqiao on 16/12/15.
 */
public class BitTest {

    @Test
    public void testBit() throws Exception {
        assert (~-3 == ~-3L);
        IntBit();
        LongBit();
    }

    public void IntBit() throws Exception {
        ExpressRunner runner = new ExpressRunner();

        IExpressContext<String, Object> context = new DefaultContext<>();
        int num = -10;
        context.put("num", num);
        testQl("num & 11", num & 11, runner, context);
        testQl("num | 11", num | 11, runner, context);
        testQl("num ^ 10", num ^ 10, runner, context);
        testQl("~num", ~num, runner, context);
        testQl("num<<2", num << 2, runner, context);
        testQl("num>>2", num >> 2, runner, context);
    }

    public void LongBit() throws Exception {
        ExpressRunner runner = new ExpressRunner();

        IExpressContext<String, Object> context = new DefaultContext<>();
        long num = -10L;
        context.put("num", num);
        testQl("num & 11", num & 11, runner, context);
        testQl("num | 11", num | 11, runner, context);
        testQl("num ^ 10", num ^ 10, runner, context);
        testQl("~num", ~num, runner, context);
        testQl("num<<2", num << 2, runner, context);
        testQl("num>>2", num >> 2, runner, context);
    }

    public void testQl(String express, Object expert, ExpressRunner runner, IExpressContext<String, Object> context)
        throws Exception {
        System.out.print(express);
        Object obj = runner.execute(express, context, null, true, false);
        System.out.println("=" + obj);
        assert (obj == expert || obj.equals(expert));
    }
}
