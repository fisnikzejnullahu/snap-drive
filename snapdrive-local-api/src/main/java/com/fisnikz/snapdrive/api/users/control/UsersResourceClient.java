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

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient(baseUri = "http://localhost:8080")
@RegisterProvider(ClientResponseExceptionMapper.class)
public interface UsersResourceClient {

    @POST
    @Path("signin")
    Response signInWithGoogle(SignInWithGoogleResponse signInWithGoogleResponse);

    @POST
    @Path("{id}/master-password")
    User createMasterPassword(@PathParam("id") String userId, CreateUserMasterPasswordRequest createUserMasterPasswordRequest);

    @PUT
    @Path("{id}")
    Response updateUser(@PathParam("id") String userId, User user);

    @POST
    @Path("login")
    Response login(UserLoginRequest loginRequest);

    @GET
    JsonObject getUserWithGivenFields(@QueryParam("email") String recipientEmail,
                                      @QueryParam("fields") String fields);


}