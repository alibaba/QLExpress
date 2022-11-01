package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class BreakContinueTest {

    @Test
    public void deadLoopTest() throws Exception {
        ExpressRunner expressRunner = new ExpressRunner();
        assertEquals(10, expressRunner.execute("for (i=1;i<10;i++){continue}i",
                new DefaultContext<>(), null, false, false));
        assertEquals(1, expressRunner.execute("for (i=1;i<10;i++){break}i",
                new DefaultContext<>(), null, false, false));
    }

    @Test
    public void doubleContinueTest() throws Exception {
        String express = "" +
                "m = 4;" +
                "for (i = 0; i < 3; i++){" +
                "  if (i==0) {" +
                "    continue;" +
                "  }" +
                "  int type = 0;" +
                "  m++;" +
                "}" +
                "m";

        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();

        Object res = runner.execute(express, context, null, true, true);
        assertEquals(6, res);
        assertEquals(6, context.get("m"));
    }
}
