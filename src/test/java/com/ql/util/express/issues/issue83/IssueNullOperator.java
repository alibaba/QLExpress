package com.ql.util.express.issues.issue83;


import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.issues.Sku;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IssueNullOperator {
    ExpressRunner runner;
    DefaultContext<String, Object> context;

    @Before
    public void setUp() {
        runner = new ExpressRunner();
        context = new DefaultContext();
        context.put("sku", new Sku(3, 100));
    }


    @Test(expected = Test.None.class)
    public void assert_null_operator_failed_when_less_than_compare() throws Exception {
        String express = "sku.price == 100 && sku.extProperties.notExistKey <= 100";

        assertFalse((Boolean) runner.execute(express, context, null, true, false, null));

    }

    @Test(expected = Test.None.class)
    public void assert_null_operator_ok_when_compare_with_string() throws Exception {
        String express = "sku.price == 100 && sku.extProperties.notExistKey == '5'";

        runner.execute(express, context, null, true, false, null);
    }

}
