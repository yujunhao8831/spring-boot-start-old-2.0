package com.aidijing.deploy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : 披荆斩棘
 * @date : 2016/12/1
 */
@Target({ElementType.METHOD})
@Retention( RetentionPolicy.RUNTIME)
public @interface AppDebug {

    String methodName () default "";
        
}
