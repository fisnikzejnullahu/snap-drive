package com.fisnikz.snapdrive.api;

import com.fisnikz.snapdrive.api.users.control.GoogleAuthService;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * @author Fisnik Zejnullahu
 */
@Provider
public class GoogleUnauthorizedExceptionMapper implements ExceptionMapper<GoogleJsonResponseException> {

    @Inject
    GoogleAuthService googleAuthService;

    @Override
    public Response toResponse(GoogleJsonResponseException e) {
        System.out.println("######################################################################");
        System.out.println("beforeacc " + googleAuthService.getAccessToken());
        try {
            googleAuthService.refreshAccessToken();
        } catch (Exception ex) {
            return Response.status(401).build();
        }
        return Response.ok(googleAuthService.getAccessToken()).build();
    }
}
