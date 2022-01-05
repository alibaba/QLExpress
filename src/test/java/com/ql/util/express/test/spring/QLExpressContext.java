package com.ql.util.express.test.spring;

import java.util.HashMap;
import java.util.Map;

import com.ql.util.express.IExpressContext;
import org.springframework.context.ApplicationContext;

@SuppressWarnings("serial")
public class QLExpressContext extends HashMap<String, Object> implements
    IExpressContext<String, Object> {

    private final ApplicationContext applicationContext;

    public QLExpressContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public QLExpressContext(Map<String, Object> properties, ApplicationContext applicationContext) {
        super(properties);
        this.applicationContext = applicationContext;
    }

    /**
     * 抽象方法：根据名称从属性列表中提取属性值
     */
    @Override
    public Object get(Object name) {
        Object result;
        result = super.get(name);
        try {
            if (result == null && this.applicationContext != null
                && this.applicationContext.containsBean((String)name)) {
                // 如果在Spring容器中包含bean，则返回String的Bean
                result = this.applicationContext.getBean((String)name);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Object put(String name, Object object) {
        return super.put(name, object);
    }
}
