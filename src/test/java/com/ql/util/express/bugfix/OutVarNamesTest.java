package com.ql.util.express.bugfix;

import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

public class OutVarNamesTest {

    @Test
    public void test() throws Exception {

        ExpressRunner expressRunner = new ExpressRunner();
        expressRunner.addMacro("求和", "max(num1,num2)");
        String exp = "求和";
        String[] vars = expressRunner.getOutVarNames(exp);

        Assert.assertEquals(vars.length,0);
    }
}
