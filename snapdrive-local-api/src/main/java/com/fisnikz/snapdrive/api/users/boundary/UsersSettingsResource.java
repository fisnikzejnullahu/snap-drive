package com.fisnikz.snapdrive.api.users.boundary;

import com.fisnikz.snapdrive.api.users.control.UsersService;
import com.fisnikz.snapdrive.api.users.entity.UpdateMasterPasswordRequest;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Fisnik Zejnullahu
 */
public class UsersSettingsResource {

    private final UsersService usersService;

    public UsersSettingsResource(UsersService usersService) {
        this.usersService = usersService;
    }

    @PUT
    @Path("master-password")
    public Response updateMasterPassword(UpdateMasterPasswordRequest updateMasterPasswordRequest) {
        return usersService.updateMasterPassword(updateMasterPasswordRequest.getNewMasterPassword(), updateMasterPasswordRequest.getOldMasterPassword());
    }

}
