package org.solmix.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a resource class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface API {
    String value();

    String[] headers() default {};
    
    /**
     * API实例查找方法，NEW：每次都新创建实例，CONTAINER：从container中查找，找不到就new
     * @return
     */
    LookupType lookup() default LookupType.NEW;
    /**
     * 配合lookup使用，为new是不生效，sharable为true时，创建完成后根据container组装注入，并放入container，然后每次取出不再做处理
     * ，如果为false，如果找不到new的实例，不放入container
     * @return
     */
    boolean sharable() default true;
}
