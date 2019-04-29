package com.example.httpclient.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpClient {
    String url() default "";

    int readTimeout() default 300000;

    int connectTimeout() default 300000;
}
