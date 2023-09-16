package com.alibaba.qlexpress4.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Inherited
@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface QLAlias {
    /**
     * @return aliases
     */
    String[] value();
}
