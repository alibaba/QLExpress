package com.ql.util.express.annotation;

import com.ql.util.express.DefaultContext;

/**
 * @author tianqiao@come-future.com
 * 2021-11-15 8:28 下午
 */
public class QLAliasContext extends DefaultContext {

    public void putAutoParams(Object... values) {
        for (Object value : values) {
            if (value.getClass().isAnnotationPresent(QLAlias.class)) {
                QLAlias[] annotations = value.getClass().getAnnotationsByType(QLAlias.class);
                for (int i = 0; i < annotations.length; i++) {
                    String[] name = annotations[i].value();
                    for (int j = 0; j < annotations.length; j++) {
                        super.put(name[j], value);
                    }
                }
            }
        }
    }
}
