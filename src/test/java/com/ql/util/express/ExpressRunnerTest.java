package com.ql.util.express;

import org.junit.Test;

import com.ql.util.express.rule.Rule;
import com.ql.util.express.rule.RuleResult;

import junit.framework.TestCase;

/**
 * Created by hongkai.wang on 2020/11/12.
 */
public class ExpressRunnerTest extends TestCase {
    @Test
    public void testParseRule() throws Exception {
        ExpressRunner expressRunner = new ExpressRunner(true, false);
        String script =
            "if(age>=18){resultCode=\"成年\";}else if(age>=10 and age<18){resultCode=\"未成年\";}else{resultCode=\"幼儿\";}";
        System.out.println(expressRunner.parseRule(script).toQl());
    }

    @Test
    public void testExecuteRule() throws Exception {
        ExpressRunner expressRunner = new ExpressRunner(true, false);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();

        String script =
            "if(age>=18){resultCode=\"成年\";}else if(age>=10 and age<18){resultCode=\"未成年\";}else{resultCode=\"幼儿\";}";
        Rule rule = expressRunner.parseRule(script);
        System.err.println("rule.toTree：\n" + rule.toTree());

        // 场景1，age>18，预计结果：成年人
        context.put("age", 20);
        // 初始化变量进去，为了可以get数据出来
        context.put("resultCode", null);
        // 后面有使用 ruleResult.getTraceMap() 分析条件的命中情况，来做数据分析，所以选择了 executeRule 而不是 execute
        RuleResult ruleResult = expressRunner.executeRule(script, context, true, false);
        System.err.println("期待if 条件命中，返回成年人，结果resultCode===" + context.get("resultCode"));
        System.err.println("成年人traceMap===" + ruleResult.getTraceMap());

        // 场景2，age<18，预计结果：未成年,实际为null
        context.put("age", 12);
        context.put("resultCode", null);
        ruleResult = expressRunner.executeRule(script, context, true, false);
        System.err.println("期望 else if条件命中，返回未成年,结果resultCode===" + context.get("resultCode"));
        System.err.println("未成年traceMap===" + ruleResult.getTraceMap());

        context.put("age", 9);
        context.put("resultCode", null);
        ruleResult = expressRunner.executeRule(script, context, true, false);
        System.err.println("期望 else 条件命中，返回幼儿,结果resultCode===" + context.get("resultCode"));
        System.err.println("幼儿traceMap===" + ruleResult.getTraceMap());
    }
}