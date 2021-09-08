package com.fisnikz.snapdrive.api.users.control;

import com.fisnikz.snapdrive.api.MyResponseExceptionMapper;
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
@RegisterProvider(MyResponseExceptionMapper.class)
public interface UsersResourceClient {

    @POST
    Response create(String request);

    @PUT
    @Path("{id}")
    Response updateUser(@PathParam("id") String userId, User user);

    @PUT
    @Path("{id}/password")
    Response updateUserPassword(@PathParam("id") String userId, JsonObject data);

    @POST
    @Path("login")
    Response login(UserLoginRequest loginRequest);

    @GET
    JsonObject getUserWithGivenFields(@QueryParam("username") String recipientUsername, @QueryParam("fields") String fields);
}