package com.alibaba.qlexpress4.spring;

import org.springframework.stereotype.Service;

/**
 * Spring Bean example service class
 */
@Service
public class HelloService {
    
    /**
     * Hello method that returns a greeting string
     * @return greeting string
     */
    public String hello(String name) {
        return "Hello, " + name + "!";
    }
}