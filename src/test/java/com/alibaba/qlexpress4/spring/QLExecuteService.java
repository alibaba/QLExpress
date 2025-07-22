package com.alibaba.qlexpress4.spring;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;

@Service
public class QLExecuteService {
    
    private final Express4Runner runner =
        new Express4Runner(InitOptions.builder().securityStrategy(QLSecurityStrategy.open()).build());
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public Object execute(String script, Map<String, Object> context) {
        QLSpringContext springContext = new QLSpringContext(context, applicationContext);
        return runner.execute(script, springContext, QLOptions.DEFAULT_OPTIONS).getResult();
    }
}