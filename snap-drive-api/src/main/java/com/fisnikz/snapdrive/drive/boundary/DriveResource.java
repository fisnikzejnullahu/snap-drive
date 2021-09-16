package com.fisnikz.snapdrive.drive.boundary;

import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.drive.control.DriveService;
import com.fisnikz.snapdrive.drive.entity.ShareFileRequest;
import com.fisnikz.snapdrive.logging.Logged;

import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Fisnik Zejnullahu
 */
@Path("drive/shares")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Logged
public class DriveResource {

    @Inject
    DriveService driveService;

    @GET
    public JsonArray userSharedFiles(@QueryParam("userId") String userId) {
        return driveService.getSharedFilesForUser(userId);
    }


    @POST
    public Response shareFile(ShareFileRequest shareFileRequest) {
        try {
            JsonObject responseData = driveService.shareFile(shareFileRequest);
            return Response.ok(responseData).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseWithJsonBodyBuilder.withInternalError();
        }
    }

    @DELETE
    public void deleteFileShares(@QueryParam("fileId") String fileId) {
        driveService.deleteFileShares(fileId);
    }


}
