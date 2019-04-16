package com.stereo.study.scan;

import java.lang.annotation.*;

/**
 * Created by liuj-ai on 2018/3/13.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisconfUpdateService {
    Class<?>[] classes() default {};

    String[] confFileKeys() default {};

    String[] itemKeys() default {};
}
