package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.runtime.context.ExpressContext;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;

/**
 * Test for for-loop compilation and execution
 */
public class ForLoopTest {

    @Test
    public void testSimpleForLoop() throws QLException {
        InitOptions initOptions = InitOptions.builder().build();
        Express4Runner runner = new Express4Runner(initOptions);
        String script = "l = []; for (int i = 3; i < 6; i++) { l.add(i); } return l;";
        QLResult result = runner.execute(script, (ExpressContext) null, QLOptions.builder().build());
        assertNotNull(result.getResult());
        System.out.println("Result: " + result.getResult());
    }

    @Test
    public void testEmptyList() throws QLException {
        InitOptions initOptions = InitOptions.builder().build();
        Express4Runner runner = new Express4Runner(initOptions);
        String script = "l = []; return l;";
        QLResult result = runner.execute(script, new HashMap<>(), QLOptions.builder().build());
        assertNotNull(result.getResult());
        System.out.println("Result: " + result.getResult());
    }
}
