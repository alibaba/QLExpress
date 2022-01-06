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
            Assert.assertTrue(e.toString().contains("at line 7"));
        }
    }

    public static String getResourceAsStream(String path) throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new Exception("classLoader中找不到资源文件:" + path);
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        bufferedReader.close();
        inputStream.close();
        return stringBuilder.toString();
    }
}
