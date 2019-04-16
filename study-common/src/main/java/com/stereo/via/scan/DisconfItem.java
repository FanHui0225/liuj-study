package com.stereo.via.scan;

import java.lang.annotation.*;

/**
 * Created by liuj-ai on 2018/3/13.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisconfItem {
    String key();

    String associateField() default "";

    String env() default "";

    String app() default "";

    String version() default "";
}
