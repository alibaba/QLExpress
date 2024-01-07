package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tianqiao on 17/7/5.
 */
public class StringTest {
    @Test
    public void testFunction() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        String exp = "a = \"11111,2222\";p = a.split(\",\");";
        IExpressContext<String, Object> context = new DefaultContext<>();
        Object result = runner.execute(exp, context, null, false, false);
        assertArrayEquals(new String[] {"11111", "2222"}, (String[]) result);
    }

    @Test
    public void stringEscapeTest() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        IExpressContext<String, Object> context = new DefaultContext<>();

        assertEquals("\"aaa\"", runner.execute("\"\\\"aaa\\\"\"", context, null, false, false));
        assertEquals("aaa'aa", runner.execute("'aaa\\'aa'", context, null, false, false));
        assertEquals("aaa\\aa", runner.execute("'aaa\\\\aa'", context, null, false, false));

        // 不认识的转义符 \a, 默认忽略掉 \
        assertEquals("aaaaa", runner.execute("'aaa\\aa'", context, null, false, false));
        assertEquals("", runner.execute("''", context, null, false, false));
        assertEquals("\n\t\r", runner.execute("'\\n\\t\\r'", context, null, false, false));
        assertEquals("\n\tm\r", runner.execute("'\\n\\t\\m\\r'", context, null, false, false));
        assertEquals("", runner.execute("\"\"", context, null, false, false));
    }
}
