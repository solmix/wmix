package org.solmix.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.solmix.rest.route.Validator;

/**
 * Annotation used to mark a resource method that responds to HTTP PATCH requests.
 * PATCH 用于资源的部分内容的更新，例如更新某一个字段。具体比如说只更新用户信息的电话号码字段
 * 部分情况下 不支持该方法  jdk7- 的 HttpUrlConnection 不支持patch
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface PATCH {
    String value() default "";

    String[] headers() default {};

    String des() default "";

    Class<? extends Validator>[] valid() default {};
}
