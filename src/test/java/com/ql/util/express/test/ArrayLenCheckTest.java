package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.exception.QLException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Author: DQinYuan
 */
public class ArrayLenCheckTest {

    @Test
    public void checkTest() throws Exception {
        QLExpressRunStrategy.setMaxArrLength(10);
        ExpressRunner runner = new ExpressRunner();
        String code = "byte[] a = new byte[11];";
        try {
            runner.execute(code, new DefaultContext<>(), null, false, false, 20);
            Assert.fail();
        } catch (QLException e) {
        }

        QLExpressRunStrategy.setMaxArrLength(-1);
        runner.execute(code, new DefaultContext<>(), null, false, false, 20);
    }

}
