package com.fisnikz.snapdrive.logging;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.System.Logger;

/**
 * @author Fisnik Zejnullahu
 */
@Interceptor
@Priority(100)
@Logged
public class LoggingInterceptor {

    @Inject
    Logger LOG;

    @AroundInvoke
    public Object logMethodCall(InvocationContext ic) throws Exception {
        LOG.log(Logger.Level.INFO, "Method: \"" + ic.getMethod().getName() + "\" of class " + ic.getTarget().getClass().getSimpleName() + " called");
        Object res = ic.proceed();
        return res;
    }
}
