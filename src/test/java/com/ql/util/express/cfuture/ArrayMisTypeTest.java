package com.ql.util.express.cfuture;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;

public class ArrayMisTypeTest {

    public String test(Object object) {
        return "object class method";
    }

    public String test(String object) {
        return "String class method";
    }

    public String test(String string, Integer integer) {
        return "String&Integer class method";
    }

    public String test(String[] string, Integer integer) {
        return "String[]&Integer class method";
    }

    public String test(String[] object) {
        return "String[] class method";
    }

    public String test(Object[] object) {
        return "Object[] class method";
    }

    @Test
    public void testFunction() throws Exception {
        ArrayMisTypeTest instance = new ArrayMisTypeTest();
        String[] strings = {"123", "456"};
        Integer[] integers = {123, 456};

        ExpressRunner runner = new ExpressRunner();
        String[][] testList = new String[][] {
            new String[] {"instance.test(strings)", instance.test(strings)},
            new String[] {"instance.test(strings[0]);", instance.test(strings[0])},
            new String[] {"instance.test(strings)", instance.test(strings)},
            new String[] {"instance.test(integers[0]);", instance.test(integers[0])},
            new String[] {"instance.test(strings[0],integers[0])", instance.test(strings[0], integers[0])},
            new String[] {"instance.test(strings,integers[0])", instance.test(strings, integers[0])},
            new String[] {"part = \"1@2@3\".split(\"@\"); return Integer.valueOf(part[2]);", "3"}
        };

        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("instance", instance);
        context.put("strings", strings);
        context.put("integers", integers);
        for (String[] test : testList) {
            System.out.println(test[0]);
            Object r = runner.execute(test[0], context, null, true, false);
            Assert.assertEquals("判定失误", r.toString(), test[1]);
        }
    }
}
