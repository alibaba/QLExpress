package com.ql.util.express.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Created by tianqiao on 16/10/27.
 */
public class SkylightRuleTransferTest {

    static Pattern pattern = Pattern.compile("[\\s]+");
    static Pattern pattern2 = Pattern.compile("(rule|RULE)[\\s]+([\\S]+)[\\s]+(name|NAME)[\\s]+([\\S]+)[\\s]+");

    public class Rule {
        public String name;
        public String code;
        public String content;
        public String ql;

        public Rule(String content) throws Exception {
            this.content = content;
            praseContent();
        }

        private void praseContent() throws Exception {

            Matcher matcher = pattern2.matcher(content);
            if (matcher.find()) {
                this.code = matcher.group(2);
                this.name = matcher.group(4);
                this.ql = matcher.replaceFirst("");
            } else {
                System.out.println("规则定义不合法");
                throw new Exception("规则定义不合法");
            }
        }

    }

    @Test
    public void helloWorld() throws Exception {
        String skylight = "rule test name 测试 for(i=0;i<10;i++){\nsum=sum+i;\n}\nreturn sum;\n";
        Rule rule = new Rule(skylight);
        System.out.println("code:" + rule.code);
        System.out.println("name:" + rule.name);
        System.out.println("ql脚本:\n" + rule.ql);

    }
}
