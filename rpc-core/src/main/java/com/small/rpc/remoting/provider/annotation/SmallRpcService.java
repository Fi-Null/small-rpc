package com.small.rpc.remoting.provider.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SmallRpcService {

    /**
     * @return
     */
    String version() default "";
}
