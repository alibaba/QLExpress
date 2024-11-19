package com.alibaba.qlexpress4.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Inherited
@Target({TYPE, FIELD, METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QLAlias {
    /**
     * @return aliases
     */
    String[] value();
}
