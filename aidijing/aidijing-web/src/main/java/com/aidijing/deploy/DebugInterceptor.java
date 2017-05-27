package com.aidijing.deploy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

/**
 * @author : 披荆斩棘
 * @date : 2016/12/1
 */
@Component
public class DebugInterceptor implements MethodInterceptor {
    

    public Object invoke ( MethodInvocation invocation ) throws Throwable {
        if ( AppConfig.isDebug() ) {
            return invocation.proceed();
        }

        return invocation.proceed();
    }




}
