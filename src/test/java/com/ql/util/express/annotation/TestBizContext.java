package com.ql.util.express.annotation;

import com.ql.util.express.DefaultContext;

/**
 * @Description
 * @Author tianqiao@come-future.com
 * @Date 2021-11-15 8:28 下午
 */
public class TestBizContext extends DefaultContext {

    @Override
    public Object get(Object key) {
        if(super.containsKey(key)) {
            return super.get(key);
        }
        for(Object value : super.values()){
            if(value.getClass().isAnnotationPresent(QLAlias.class)){
                QLAlias[] annotations = value.getClass().getAnnotationsByType(QLAlias.class);
                for(int i=0;i<annotations.length;i++) {
                    String[] name = ((QLAlias) annotations[i]).value();
                    for(int j=0;j<annotations.length;j++) {
                        if(name[j].equals(key)){
                            return value;
                        }
                    }
                }
            }
        }
        return super.get(key);
    }
}
