package com.ql.util.express.rule;

import com.ql.util.express.ExpressRunner;
import org.junit.Test;

/**
 * Created by tianqiao on 16/12/21.
 */
public class RuleConditionTest {
    
    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        parseRule("max(metric1,30) < 20 and min(metric2,40) > 30",runner);
        
    }
    
    private void parseRule(String s, ExpressRunner runner) throws Exception {
        Condition condition = runner.parseContition(s);
        printCondition(condition);
    }
    
    private void printCondition(Condition root)
    {
        if(root.getChildren()==null){
            System.out.println(root);
            return;
        }
        for(Condition child : root.getChildren()){
            printCondition(child);
        }
        
    }
}
