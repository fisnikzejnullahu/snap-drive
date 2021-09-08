package com.fisnikz.snapdrive.logging;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.System.Logger;

/**
 * @author Fisnik Zejnullahu
 */
@Dependent
public class LoggingSystem {

    @Produces
    public Logger produceLogger(InjectionPoint ip) {
        return System.getLogger(ip.getMember().getDeclaringClass().getName());
    }
}
