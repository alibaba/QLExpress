package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.TestSuiteRunner;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author TaoKan
 * @Date 2022/9/24 下午3:52
 */
public class TestSuiteCaseForProperty {
    @Test
    public void test() throws IOException, URISyntaxException {
        TestSuiteRunner testSuiteRunner = new TestSuiteRunner();
        testSuiteRunner.before();
        Path publicStatic = Paths.get(getClass().getClassLoader()
                .getResource("testsuite").toURI()).resolve("java/property/array_length_get.ql");
//        testSuiteRunner.testFilePath("java/property/public_static.ql");
        testSuiteRunner.handleFile(publicStatic,publicStatic.toString(),true);
//        testSuiteRunner.testFilePath("java/property/private_member_attr_getter.ql");

    }
}
