package com.alibaba.qlexpress4.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Author: TaoKan
 */
@Inherited
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QLField {
    /**
     * @return aliases
     */
    String[] value();
}
