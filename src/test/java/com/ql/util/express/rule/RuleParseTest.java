package com.ql.util.express.rule;

import com.ql.util.express.ExpressRunner;
import org.junit.Test;

/**
 * Created by tianqiao on 16/12/21.
 */
public class RuleParseTest {
    
    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        parseRule("rule 'RULE_FEATURESET_BOOK_TOP_CAT_FEATURE' name '书籍类目增加特征topCategoryId'\n" +
                "when inCategory.mainCategory.categoryId == 33\n" +
                "then add(inItem.features,'topCategoryId',inCategory.mainCategory.categoryId.cast2String());\n" +
                "when inCategory.mainCategory.categoryId != 33\n" +
                "then del(inItem.features,'topCategoryId');",runner);
        
    }
    
    private void parseRule(String s, ExpressRunner runner) throws Exception {
        Rule rule = runner.parseRule(s);
        System.out.println(rule.toSkylight());
        System.out.println(rule.toTree());
    }
}
