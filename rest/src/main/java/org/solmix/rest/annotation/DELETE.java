package org.solmix.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.solmix.rest.route.Validator;

/**
 * Annotation used to mark a resource method that responds to HTTP DELETE requests.
 * 删除某一个资源。基本上这个也很少见，不过还是有一些地方比如amazon的S3云服务里面就用的这个方法来删除资源。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DELETE {
    String value() default "";

    String[] headers() default {};

    String des() default "";

    Class<? extends Validator>[] valid() default {};
}
