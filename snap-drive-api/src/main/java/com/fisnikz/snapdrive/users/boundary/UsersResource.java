package com.fisnikz.snapdrive.users.boundary;

import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.control.UsersService;
import com.fisnikz.snapdrive.users.entity.CreateUserRequest;
import com.fisnikz.snapdrive.users.entity.User;
import com.fisnikz.snapdrive.users.entity.UserLoginRequest;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Fisnik Zejnullahu
 */
@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Logged
public class UsersResource {

    @Inject
    UsersService usersService;

    @POST
    public Response create(CreateUserRequest createUserRequest) {
        return usersService.create(createUserRequest);
//        CompletableFuture.runAsync(response::resume);
//        CompletableFuture
//                .supplyAsync(() -> usersService.create(createUserRequest))
//                .thenAccept(response::resume);
    }

    @PUT
    @Path("{id}/password")
    public Response updateUserPassword(@PathParam("id") String userId, JsonObject data) {
        return usersService.updatePassword(userId, data.getString("newPassword"));
    }

    @PUT
    @Path("{id}")
    public JsonObject updateUser(@PathParam("id") String userId, User user) {
        return Json.createObjectBuilder().add("updated", usersService.updateUser(userId, user)).build();
    }

    @POST
    @Path("login")
    public User login(UserLoginRequest loginRequest) {
        return usersService.login(loginRequest);
    }

    @GET
    public JsonObject getUserWithGivenFields(@QueryParam("username") String username, @QueryParam("fields") String fields) {
        return usersService.getUserWithGivenFields(username, fields);
    }
}
