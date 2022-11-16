package com.ql.util.express.issue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class Issue_187 extends IssueBase {
    @Test(expected = RuntimeException.class)
    public void test() {
        Map<String, Object> context = new HashMap<>();
        context.put("tz_0241_01", "a");
        String script = "tz_0241_01 <> 'a'";
        Object result = execute(script, context);
        System.out.println("result = " + result);
    }
}
