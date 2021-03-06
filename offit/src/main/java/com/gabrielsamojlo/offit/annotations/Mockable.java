package com.gabrielsamojlo.offit.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface Mockable {

    String jsonPath() default "";

    String tag() default "";

    int responseCode() default 0;

    int responseTime() default 0;

}
