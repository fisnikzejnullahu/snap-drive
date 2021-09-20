package com.fisnikz.snapdrive.api.drive.boundary;

import com.fisnikz.snapdrive.api.users.control.LoggedInUserInfo;

import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.System.Logger;

/**
 * @author Fisnik Zejnullahu
 */
@Provider
@PreMatching
public class ClientFilter implements ContainerRequestFilter {

    @Inject
    LoggedInUserInfo loggedInUserInfo;

    @Inject
    Logger LOG;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        LOG.log(Logger.Level.INFO, "Filtering New Client Request...");

        var body = Json.createObjectBuilder()
                .add("message", "please login!")
                .build();

        if (requestContext.getUriInfo().getPath().startsWith("/drive") && !this.loggedInUserInfo.isLoggedIn()) {
            requestContext.abortWith(Response.status(401).entity(body).build());
        }
    }
}
