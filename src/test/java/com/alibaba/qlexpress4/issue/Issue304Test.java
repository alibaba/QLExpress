package com.alibaba.qlexpress4.issue;

import java.util.HashMap;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.context.MapExpressContext;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author 冰够
 */
public class Issue304Test {
    @Test
    public void test() throws Exception {
        String script
            = "(((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((1+1))))))))))))"
            + ")))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))";
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        QLambda qLambda = express4Runner.parseToLambda(script, new MapExpressContext(new HashMap<>()),
            QLOptions.DEFAULT_OPTIONS);
        Object result = qLambda.get();
        Assert.assertEquals(2, result);
    }
}
