package com.stereo.study.scan;

import java.lang.annotation.*;

/**
 * Created by liuj-ai on 2018/3/13.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisconfActiveBackupService {
    Class<?>[] classes() default {};

    String[] itemKeys() default {};
}
