package com.fisnikz.snapdrive.api.users.boundary;

import com.fisnikz.snapdrive.api.users.control.UsersService;
import com.fisnikz.snapdrive.api.users.entity.UpdateMasterPasswordRequest;

import javax.json.JsonObject;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Fisnik Zejnullahu
 */
public class UsersSettingsResource {

    private UsersService usersService;

    public UsersSettingsResource(UsersService usersService) {
        this.usersService = usersService;
    }

    @PUT
    @Path("master-password")
    public Response updateMasterPassword(UpdateMasterPasswordRequest updateMasterPasswordRequest) {
        return usersService.updateMasterPassword(updateMasterPasswordRequest.getNewMasterPassword(), updateMasterPasswordRequest.getOldMasterPassword());

//        usersService.create(createUserRequest);
//        CompletableFuture.runAsync(response::resume);
//        CompletableFuture
//                .supplyAsync(() -> usersService.create(createUserRequest))
//                .thenAccept(response::resume);
    }

    @PUT
    @Path("profile")
    public Response updateProfile(JsonObject data) {
        return usersService.updateProfile(data.getString("newUsername"));
    }

    @PUT
    @Path("password")
    public Response updatePassword(JsonObject data) {
        return usersService.updatePassword(data);
    }
}
