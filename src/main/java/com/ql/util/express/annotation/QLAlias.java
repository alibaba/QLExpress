package com.ql.util.express.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

@Inherited
@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface QLAlias {
    /**
     * 注解内容
     */
    String[] value();
}
