package com.alibaba.qlexpress4.test.issue;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author 冰够
 */
public class Issue318Test {
    @Test
    public void test() throws Exception {
        Express4Runner runner = new Express4Runner(
            InitOptions.builder().securityStrategy(QLSecurityStrategy.open()).build());
        Map<String, Object> context = new HashMap<>();

        Student student = new Student();
        student.name = "张三";
        student.alias = "zhangsan";
        context.put("student", student);
        Object result = runner.execute("student.name == \"张三\"", context, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue((Boolean)result);

        result = runner.execute("student.alias == \"zhangsan\"", context, QLOptions.DEFAULT_OPTIONS);
        Assert.assertTrue((Boolean)result);
    }

    public static class Student {
        /**
         * 默认不支持属性直接访问，必须要有getter方法
         *
         * @return
         */
        public String name;
        public String alias;

        public String getName() {
            return name;
        }

        public String getAlias() {
            return alias;
        }
    }
}