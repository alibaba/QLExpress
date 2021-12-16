package com.ql.util.express.test;

import java.util.HashMap;
import java.util.Map;

import com.ql.util.express.IExpressContext;
import org.springframework.context.ApplicationContext;

@SuppressWarnings("serial")
public class ExpressContextExample extends HashMap<String, Object> implements IExpressContext<String, Object> {

    private final ApplicationContext applicationContext;

    public ExpressContextExample(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ExpressContextExample(Map<String, Object> properties, ApplicationContext applicationContext) {
        super(properties);
        this.applicationContext = applicationContext;
    }

    /**
     * 抽象方法：根据名称从属性列表中提取属性值
     */
    @Override
    public Object get(Object name) {
        Object result;
        if (((String)name).equalsIgnoreCase("三星卖家")) {
            result = Boolean.TRUE;
        } else if (((String)name).equalsIgnoreCase("消保用户")) {
            result = Boolean.TRUE;
        } else {
            result = super.get(name);
        }
        try {
            if (result == null && this.applicationContext != null && this.applicationContext.containsBean((String)name)) {
                //如果在Spring容器中包含bean，则返回String的Bean
                result = this.applicationContext.getBean((String)name);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Object put(String name, Object object) {
        if (name.equalsIgnoreCase("myDbData")) {
            throw new RuntimeException("没有实现");
        }
        return super.put(name, object);
    }
}
