package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

/**
 * @author 冰够
 * @date 2025/07/22
 */
public class Express4RunnerBingoTest {
    @Test
    public void test_map() throws Exception {
        String script = "\n"
            + "map = new HashMap();\n"
            + "map.put(\"key1\", \"value1\");\n"
            + "return map;";
        System.out.println("script = " + script);
        ExpressRunner expressRunner = new ExpressRunner();
        DefaultContext<String, Object> defaultContext = new DefaultContext<>();
        Object result = expressRunner.execute(script, defaultContext, null, true, false);
        System.out.println("result = " + result);
    }
}
