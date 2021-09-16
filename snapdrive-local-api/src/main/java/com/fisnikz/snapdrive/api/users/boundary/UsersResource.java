package com.fisnikz.snapdrive.api.users.boundary;

import com.fisnikz.snapdrive.api.users.control.GoogleAuthService;
import com.fisnikz.snapdrive.api.users.control.UsersService;
import com.fisnikz.snapdrive.api.users.entity.CreateUserMasterPasswordRequest;
import com.fisnikz.snapdrive.api.users.entity.LoggedInUserInfo;
import com.fisnikz.snapdrive.api.users.entity.User;
import com.fisnikz.snapdrive.logging.Logged;

import javax.inject.Inject;
import javax.json.Json;
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

    @Inject
    LoggedInUserInfo loggedInUserInfo;

    @POST
    @Path("signin")
    public Response signInWithGoogle(@QueryParam("authCode") String authorizationCode) {
        return usersService.signInWithGoogle(authorizationCode);
    }

    @POST
    @Path("master-password")
    public Response createUserMasterPassword(CreateUserMasterPasswordRequest createUserMasterPasswordRequest) {
        try {
            User user = usersService.createMasterPassword(loggedInUserInfo.getUser().getId(), createUserMasterPasswordRequest.getMasterPassword());
            loggedInUserInfo.setUser(user);
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof WebApplicationException) {
                return ((WebApplicationException) e).getResponse();
            }

            JsonObject data = Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", "Something went wrong!")
                    .build();
            return Response.status(500).entity(data).build();
        }
//        CompletableFuture.runAsync(response::resume);
//        CompletableFuture
//                .supplyAsync(() -> usersService.signInWithGoogle(createUserRequest))
//                .thenAccept(response::resume);
    }

    @Path("settings")
    public UsersSettingsResource updateSettings() {
        return new UsersSettingsResource(usersService);
    }

    @POST
    @Path("logout")
    public void logout() {
        usersService.logout();
    }

}
