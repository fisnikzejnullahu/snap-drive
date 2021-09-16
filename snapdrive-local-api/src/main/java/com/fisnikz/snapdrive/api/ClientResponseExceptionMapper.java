package com.fisnikz.snapdrive.api;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
public class ClientResponseExceptionMapper implements ResponseExceptionMapper<WebApplicationException> {

    @Override
    public WebApplicationException toThrowable(Response response) {
        System.out.println("MAPPING");

        JsonObject body = response.readEntity(JsonObject.class);
        System.out.println(body);

        WebApplicationException ex = new WebApplicationException(Response.status(response.getStatus())
                .entity(body)
                .build());

        ex.printStackTrace();
        return ex;
    }
}