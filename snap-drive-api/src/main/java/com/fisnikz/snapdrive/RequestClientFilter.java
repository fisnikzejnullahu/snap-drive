package com.fisnikz.snapdrive;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.System.Logger;
import java.time.LocalDateTime;

@Provider
@PreMatching
public class RequestClientFilter implements ContainerRequestFilter {

    @Inject
    Logger LOG;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOG.log(Logger.Level.INFO, "Filtering New Client Request...");
        System.out.println(requestContext.getUriInfo().getAbsolutePath());
        requestContext.getHeaders().forEach((key, val) -> {
            System.out.println(key + ": " + val);
        });
        System.out.println("Method: " + requestContext.getMethod());
        LOG.log(Logger.Level.INFO, "Filtering Done!");
    }
}
