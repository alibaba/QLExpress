package com.ql.util.express.annotation;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

/**
 * @Description
 * @Author tianqiao@come-future.com
 * @Date 2021-11-15 5:51 下午
 */
public class AnnotationTest {

    @Test
    public void testQLAlias() throws Exception{
        ExpressRunner runner = new ExpressRunner();
        String exps[] = new String[]{
                "患者.birth",
                "患者.生日()",
                "患者.birth==患者.出生年月()",
                "患者.生日()==患者.生日",
                "患者.姓名 + ' 今年 '+ 患者.获取年龄() +' 岁啦！'",
        };
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        Person person = new Person();
        person.setName("老王");
        person.setSex("男");
        context.put("患者",person);
        for(String exp:exps) {
            Object result = runner.execute(exp, context, null, false, false);
            System.out.println(result);
        }
    }

    @Test
    public void testQLAlias2() throws Exception{
        ExpressRunner runner = new ExpressRunner();
        String exps[] = new String[]{
                "患者.birth",
                "患者.生日()",
                "患者.birth==患者.出生年月()",
                "患者.生日()==患者.生日",
                "患者.姓名 + ' 今年 '+ 患者.获取年龄() +' 岁啦！'",
        };
        IExpressContext<String, Object> context = new TestBizContext();
        Person person = new Person();
        person.setName("老王");
        person.setSex("男");
        context.put("person",person);
        for(String exp:exps) {
            Object result = runner.execute(exp, context, null, false, false);
            System.out.println(result);
        }
    }
}
