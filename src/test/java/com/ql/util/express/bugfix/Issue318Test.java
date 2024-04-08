package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.exception.QLCompileException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author 冰够
 */
public class Issue318Test {
    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();

        Student student = new Student();
        student.name = "张三";
        student.alias = "zhangsan";
        context.put("student", student);
        Object result = runner.execute("student.name == \"张三\"", context, null, true, true);
        Assert.assertTrue((Boolean)result);

        Assert.assertThrows(QLCompileException.class,
            () -> runner.execute("student.alias == \"zhangsan\"", context, null, true, true));
    }

    public static class Student {
        public String name;
        public String alias;

        /**
         * 不支持属性直接访问，必须要有getter方法
         *
         * @return
         */
        public String getName() {
            return name;
        }

        public String getAlias() {
            return alias;
        }
    }
}
