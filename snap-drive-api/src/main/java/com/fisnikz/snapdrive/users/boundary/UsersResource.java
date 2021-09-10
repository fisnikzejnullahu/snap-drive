package com.fisnikz.snapdrive.users.boundary;

import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.control.UsersService;
import com.fisnikz.snapdrive.users.entity.CreateUserRequest;
import com.fisnikz.snapdrive.users.entity.User;
import com.fisnikz.snapdrive.users.entity.UserLoginRequest;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

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
        String userId = usersService.create(createUserRequest);
        return Response.created(URI.create("/users/" + userId)).build();
    }

    @PUT
    @Path("{id}")
    public JsonObject updateUser(@PathParam("id") String userId, User user) {
        return Json.createObjectBuilder().add("updated", usersService.updateUser(userId, user)).build();
    }

    @PUT
    @Path("{id}/password")
    public Response updateUserPassword(@PathParam("id") String userId, JsonObject data) {
        return usersService.updatePassword(userId, data.getString("newPassword"));
    }


    @POST
    @Path("login")
    public User login(UserLoginRequest loginRequest) {
        return usersService.login(loginRequest);
    }

    @GET
    public JsonObject getUserWithGivenFields(@QueryParam("id") String id,
                                             @QueryParam("username") String username,
                                             @QueryParam("fields") String fields) {

        // if no username and id are given then return bad response
        // if there is username then find user based on username
        // if there is id then find user based on id
        if ((username == null || username.trim().isEmpty())
                &&
            (id == null || id.trim().isEmpty())) {
            System.out.println("1");
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(400, "Please provide user's ID or Username"));
        }

        if (username != null || !username.trim().isEmpty()) {
            System.out.println("2");
            User user = usersService.getUserByUsernameOrThrow404(username);
            return usersService.getUserWithGivenFields(user, fields);
        }

        User user = usersService.getUserByIdOrThrow404(id);
            System.out.println("3");
        return usersService.getUserWithGivenFields(user, fields);
    }
}
