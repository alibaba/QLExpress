package com.alibaba.qlexpress4.spring;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * HelloService unit test class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class)
public class SpringDemoTest {
    
    @Autowired
    private QLExecuteService qlExecuteService;
    
    @Test
    public void qlExecuteWithSpringContextTest() {
        Map<String, Object> context = new HashMap<>();
        context.put("name", "Wang");
        String result = (String)qlExecuteService.execute("helloService.hello(name)", context);
        Assert.assertEquals("Hello, Wang!", result);
    }
}