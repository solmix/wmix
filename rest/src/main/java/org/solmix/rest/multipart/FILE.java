package org.solmix.rest.multipart;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.solmix.rest.util.DefaultFileRenamer;
import org.solmix.rest.util.FileRenamer;

/**
 * 上传文件时使用该注解 设置文件相关参数
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface FILE {
    String dir() default "";//文件上传的目录

    boolean overwrite() default false;//遇到同名文件是否覆盖,适合客户端控制文件名

    Class<? extends FileRenamer> renamer() default DefaultFileRenamer.class;//对文件名字进行重命名处理

    int max() default -1;//上传的大小限制，默认最大10M

    String encoding() default "";//文件编码格式

    String[] allows() default {}; //file content type eg. text/xml 允许上传的文件类型
}
