package com.ql.util.express.cfuture;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;

public class ArrayMisType {

    public String test(Object object)
    {
        return "object class method";
    }

    public String test(String object)
    {
        return "String class method";
    }

    public String test(String astr,Integer ainteger)
    {
        return "String&Integer class method";
    }

    public String test(String[] astr,Integer ainteger)
    {
        return "String[]&Integer class method";
    }

    public String test(String[] object)
    {
        return "String[] class method";
    }

    public String test(Object[] object)
    {
        return "Object[] class method";
    }

    @Test
    public void testFunction() throws Exception {

        ArrayMisType instance = new ArrayMisType();
        String[] strings = {"123","456"};
        Integer[] integers = {123,456};

        ExpressRunner runner = new ExpressRunner();
        String[][] testList= new String[][]{
//            new String[]{"instance.test(strings)",instance.test(strings)},
//            new String[]{"instance.test(strings[0]);",instance.test(strings[0])},
//            new String[]{"instance.test(strings)",instance.test(strings)},
//            new String[]{"instance.test(integers[0]);",instance.test(integers[0])},
//            new String[]{"instance.test(strings[0],integers[0])",instance.test(strings[0],integers[0])},
            new String[]{"instance.test(strings,integers[0])",instance.test(strings,integers[0])},
        };

        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("instance", instance);
        context.put("strings", strings);
        context.put("integers", integers);
        for(String[] test:testList) {
            System.out.println(test[0]);
            Object r = runner.execute(test[0], context, null, true, false);
            Assert.assertTrue("判定失误", r.equals(test[1]));
        }
    }
}
