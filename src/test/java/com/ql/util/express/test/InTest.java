package com.ql.util.express.test;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

public class InTest {
    @Test
    public void testOperatorIn() throws Exception {
        String express1 = "2 in (2, 3) ";
        String express2 = "2 in a";
        String express3 = "2 in b";

        ExpressRunner runner = new ExpressRunner(true, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        int[] a = {1, 2, 3};
        context.put("a", a);
        List<Integer> b = new ArrayList<>();
        b.add(2);
        b.add(3);

        context.put("b", b);
        System.out.println(runner.execute(express1, context, null, false, false));
        System.out.println(runner.execute(express2, context, null, false, false));
        System.out.println(runner.execute(express3, context, null, false, false));
    }
}
