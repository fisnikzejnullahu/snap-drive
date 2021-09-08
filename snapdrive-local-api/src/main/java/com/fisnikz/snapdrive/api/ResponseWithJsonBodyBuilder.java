package com.fisnikz.snapdrive.api;

import javax.json.Json;
import javax.ws.rs.core.Response;

/**
 * @author Fisnik Zejnullahu
 */
public class ResponseWithJsonBodyBuilder {

    public static Response withInformation(int status, String message) {
        return Response.status(status)
                .entity(Json.createObjectBuilder()
                        .add("message", message)
                        .build())
                .build();
    }

    public static Response withInternalError() {
        return Response.status(500)
                .entity(Json.createObjectBuilder()
                        .add("message", "Something went wrong!")
                        .build())
                .build();
    }
}
