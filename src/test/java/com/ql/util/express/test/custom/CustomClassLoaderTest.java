package com.ql.util.express.test.custom;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.config.QLExpressRunStrategy;
import junit.framework.TestCase;
import org.junit.Test;

public class CustomClassLoaderTest {
    private void setCustomClassLoader() throws MalformedURLException {
        String userDir = System.getProperty("user.dir");
        String classesPath = userDir + "/src/test/resources/classes/";
        System.out.println("classesPath = " + classesPath);
        File file = new File(classesPath);
        URL url = file.toURI().toURL();
        ClassLoader customClassLoader = new URLClassLoader(new URL[] {url}, null);
        QLExpressRunStrategy.setCustomClassLoader(customClassLoader);
    }

    @Test
    public void test_with_custom_classLoader() throws Exception {
        ClassLoader previousCustomClassLoader = QLExpressRunStrategy.getCustomClassLoader();
        try {
            setCustomClassLoader();

            // 如下import类来自com.ql.util.express.test.CustomClassLoaderTest.SelfDefineObject1，如果要修改，则取消这个类的注释，
            // 然后编译，将编译后的class文件复制到/src/test/resources/classes/文件夹下
            String expression = "\r\n"
                + "import com.ql.util.express.test.custom.SelfDefineObject1;\r\n"
                + "return SelfDefineObject1.getValue();";
            System.out.println("expression = " + expression);
            ExpressRunner expressRunner = new ExpressRunner();
            IExpressContext<String, Object> context = new DefaultContext<>();
            Object result = expressRunner.execute(expression, context, null, true, false);
            TestCase.assertEquals("success1", result);
        } finally {
            QLExpressRunStrategy.setCustomClassLoader(previousCustomClassLoader);
        }
    }

    @Test
    public void test_without_custom_classLoader() throws Exception {
        String expression = "\r\n"
            + "import com.ql.util.express.test.custom.SelfDefineObject2;\r\n"
            + "return SelfDefineObject2.getValue();";
        System.out.println("expression = " + expression);
        ExpressRunner expressRunner = new ExpressRunner();
        IExpressContext<String, Object> context = new DefaultContext<>();
        Object result = expressRunner.execute(expression, context, null, true, false);
        TestCase.assertEquals("success2", result);
    }
}
