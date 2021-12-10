package com.ql.util.express.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tianqiao on 17/4/1.
 */
public class PrintLineExceptionTest {
    @Test
    public void testLoadFromFile() throws Exception {
        String script = getResourceAsStream("lineTest.ql");
        ExpressRunner runner = new ExpressRunner(false, false);
        IExpressContext<String, Object> context = new DefaultContext<>();
        try {
            Object obj = runner.execute(script, context, null, true, false);
            System.out.println(obj);
        } catch (Exception e) {
            //e.printStackTrace();
            Assert.assertTrue(e.toString().contains("at line 7"));
        }
    }

    public static String getResourceAsStream(String path) throws Exception {
        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(path);
        if (in == null) {
            throw new Exception("classLoader中找不到资源文件:" + path);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String tmpStr;
        while ((tmpStr = reader.readLine()) != null) {
            builder.append(tmpStr).append("\n");
        }
        reader.close();
        in.close();
        return builder.toString();
    }
}
