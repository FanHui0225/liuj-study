package com.stereo.via.scan;

import java.lang.annotation.*;

/**
 * Created by liuj-ai on 2018/3/13.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisconfFile {
    String filename();

    String env() default "";

    String version() default "";

    String app() default "";

    String targetDirPath() default "";
}
