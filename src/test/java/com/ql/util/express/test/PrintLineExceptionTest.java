package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by tianqiao on 17/4/1.
 */
public class PrintLineExceptionTest {
    
    @Test
    public void testLoadFromFile() throws Exception {
        
        String script = getResourceAsStream("lineTest.ql");
        ExpressRunner runner = new ExpressRunner(false, false);
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        try {
            Object obj = runner.execute(script, context, null, true, false);
            System.out.println(obj);
        }catch (Exception e){
//            e.printStackTrace();
            Assert.assertTrue(e.toString().contains("at line 7"));;
        }
    }
    
    public static String getResourceAsStream(String path) throws Exception {
        InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(path);
        if (in == null) {
            throw new Exception("classLoader中找不到资源文件:" + path);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"));
        StringBuilder builder = new StringBuilder();
        String tmpStr = null;
        while ((tmpStr = reader.readLine()) != null) {
            builder.append(tmpStr).append("\n");
        }
        reader.close();
        in.close();
        return builder.toString();
    }
}
