package com.github.jjjzzzqqq.mini.db.router.annotation;


import java.lang.annotation.*;

@Documented//在Java文档中是否保留注解信息
@Retention(RetentionPolicy.RUNTIME)//在运行期保留，可通过反射获取注解信息
@Target({ElementType.TYPE, ElementType.METHOD})//注解作用的位置，Type表示类，Method表示方法
public @interface DBRouter {

    /**
     * 分库分表Hash字段
     */
    String key() default "";
}
