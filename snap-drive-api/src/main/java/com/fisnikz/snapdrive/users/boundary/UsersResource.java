package com.fisnikz.snapdrive.users.boundary;

import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.control.UsersService;
import com.fisnikz.snapdrive.users.entity.CreateUserMasterPasswordRequest;
import com.fisnikz.snapdrive.users.entity.SignInWithGoogleRequest;
import com.fisnikz.snapdrive.users.entity.User;

import javax.inject.Inject;
import javax.json.JsonObject;
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
    @Path("signin")
    public Response signInWithGoogle(SignInWithGoogleRequest signInWithGoogleRequest) {
        return usersService.signInWithGoogle(signInWithGoogleRequest.getEmail(), signInWithGoogleRequest.getGoogleId());
    }

    @PUT
    public Response addOrUpdateUser(User user) {
        return usersService.addOrUpdate(user);
    }

    @POST
    @Path("{id}/master-password")
    public User createUserMasterPassword(@PathParam("id") String userId, CreateUserMasterPasswordRequest createUserMasterPasswordRequest) {
        return usersService.createMasterPassword(userId, createUserMasterPasswordRequest);
    }

    @GET
    public JsonObject getUserWithGivenFields(@QueryParam("email") String userEmail,
                                             @QueryParam("fields") String fields) {

        if (userEmail != null && !userEmail.trim().isEmpty()) {
            return usersService.getUserWithGivenFields(userEmail, fields);
        }

        throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(400, "Please provide user's Email"));
    }
}
