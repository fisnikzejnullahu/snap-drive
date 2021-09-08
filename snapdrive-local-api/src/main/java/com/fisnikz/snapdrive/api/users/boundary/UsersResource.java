package com.fisnikz.snapdrive.api.users.boundary;

import com.fisnikz.snapdrive.api.users.control.UsersService;
import com.fisnikz.snapdrive.api.users.entity.CreateUserRequest;
import com.fisnikz.snapdrive.api.users.entity.UserLoginRequest;
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

    @POST
    public Response create(CreateUserRequest createUserRequest) {
        Response create = null;
        try {
            create = usersService.create(createUserRequest);
            return create;
        } catch (Exception e) {
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
//                .supplyAsync(() -> usersService.create(createUserRequest))
//                .thenAccept(response::resume);
    }

    @Path("settings")
    public UsersSettingsResource updateSettings() {
        return new UsersSettingsResource(usersService);
    }

    @POST
    @Path("login")
    public JsonObject login(UserLoginRequest loginRequest) {
        return usersService.login(loginRequest);
    }

    @POST
    @Path("unlock")
    public JsonObject unlockFiles(JsonObject masterPasswordObj) {
        return usersService.unlockFiles(masterPasswordObj.getString("masterPassword"));
    }

    @POST
    @Path("logout")
    public void logout() {
        usersService.logout();
    }

}
