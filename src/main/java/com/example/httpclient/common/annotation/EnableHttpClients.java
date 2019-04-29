package com.example.httpclient.common.annotation;

import com.example.httpclient.httpClient.HttpClientsRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(HttpClientsRegistrar.class)
public @interface EnableHttpClients {

    /**
     * 扫描的类
     */
    String[] value() default {};

    /**
     * 扫描路径
     * 默认注解声明所在类
     */
    String[] basePackages() default {};

    /**
     * 扫描的基类
     */
    Class<?>[] basePackageClasses() default {};
}
