package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.exception.QLTimeOutException;
import org.junit.Test;

/**
 * @author tianqiao@taobao.com
 * @since 2019/6/18 10:52 AM
 */
public class TimeOutExceptionTest {


    private static String[] expressList = new String[]{
            "sum=0;for(i=0;i<1000000000;i++){sum=sum+i;}return sum;",
            "for(i=1;i<10;i++){System.out.println('loop time:'+i);Thread.sleep(300);}"
    };

    @Test
    public void test() throws Exception {

        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();

        for(String express : expressList) {
            try {

                Object r = runner.execute(express, context, null, true, false, 1000);
                System.out.println(r);
                throw new Exception("没有捕获到超时异常");
            } catch (QLTimeOutException e) {
                System.out.println(e);
            }
        }
    }
}
