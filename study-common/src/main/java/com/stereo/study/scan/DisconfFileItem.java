package com.stereo.study.scan;

import java.lang.annotation.*;

/**
 * Created by liuj-ai on 2018/3/13.
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisconfFileItem {
    String name();

    String associateField() default "";
}
