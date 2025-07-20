package com.alibaba.qlexpress4.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring test configuration class
 */
@Configuration
@ComponentScan(basePackages = "com.alibaba.qlexpress4.spring")
public class SpringTestConfig {
    // Configuration class, enables component scanning
}