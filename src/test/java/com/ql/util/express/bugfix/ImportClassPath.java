package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tianqiao on 17/6/23.
 */
public class ImportClassPath {
    
    @Test
    public void test() {
    
        ExpressRunner runner = new ExpressRunner();
        String exp ="return new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").format(new Date())";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        Object result = null;
        try {
            result = runner.execute(exp,context,null,false,false);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("SimpleDateFormat 没有定义，此处应该报错");
            //e.printStackTrace();
            Assert.assertTrue(true);
            return;
            
        }
        Assert.assertTrue(false);
    }
    
    @Test
    public void test2() throws Exception {
        
        ExpressRunner runner = new ExpressRunner();
        String exp ="return new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").format(new Date())";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        Object result = null;
        result = runner.execute(exp,context,null,false,false);
        System.out.println(result);
    }
    
    @Test
    public void test3() throws Exception {
        
        ExpressRunner runner = new ExpressRunner();
        String exp ="import java.text.SimpleDateFormat; return new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").format(new Date())";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        Object result = null;
        result = runner.execute(exp,context,null,false,false);
        System.out.println(result);
    }
}
