package com.fisnikz.snapdrive.api.users.control;

import com.fisnikz.snapdrive.api.ClientResponseExceptionMapper;
import com.fisnikz.snapdrive.api.users.entity.CreateUserMasterPasswordRequest;
import com.fisnikz.snapdrive.api.users.entity.SignInWithGoogleResponse;
import com.fisnikz.snapdrive.api.users.entity.User;
import com.fisnikz.snapdrive.api.users.entity.UserLoginRequest;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RegisterRestClient(configKey = "users")
@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterProvider(ClientResponseExceptionMapper.class)
public interface UsersResourceClient {

    @POST
    @Path("signin")
    Response signInWithGoogle(SignInWithGoogleResponse signInWithGoogleResponse);

    @POST
    @Path("{id}/master-password")
    User createMasterPassword(@PathParam("id") String userId, CreateUserMasterPasswordRequest createUserMasterPasswordRequest);

    @PUT
    Response addOrUpdateUser(User user);

    @GET
    JsonObject getUserWithGivenFields(@QueryParam("email") String recipientEmail,
                                      @QueryParam("fields") String fields);


}