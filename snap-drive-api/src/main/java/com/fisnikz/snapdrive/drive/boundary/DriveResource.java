package com.fisnikz.snapdrive.drive.boundary;

import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.drive.control.DriveService;
import com.fisnikz.snapdrive.drive.entity.ShareFileRequest;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.drive.entity.DriveFile;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * @author Fisnik Zejnullahu
 */
@Path("drive")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Logged
public class DriveResource {

    @Inject
    DriveService driveService;

    @GET
    public JsonArray getFiles(@QueryParam("userId") String userId) {
        return driveService.getFilesOfUser(userId);
    }

    @POST
    public Response create(DriveFile driveFile) {
        String id = driveService.create(driveFile);
        return Response.created(URI.create("/drive/" + id)).build();
    }

    @PUT
    @Path("{fileId}")
    public DriveFile update(@PathParam("fileId") String fileId, DriveFile driveFile) {
        return driveService.update(fileId, driveFile);
    }

    @GET
    @Path("{id}")
    public JsonObject getFile(@PathParam("id") String fileId) {
        return driveService.getFile(fileId);
    }

    @DELETE
    @Path("{fileId}")
    public JsonObject delete(@PathParam("fileId") String fileId) {
        return driveService.deleteFile(fileId);


    }

    @POST
    @Path("file-shares")
    public Response shareFile(ShareFileRequest shareFileRequest) {
        try {
            JsonObject responseData = driveService.shareFile(shareFileRequest);
            return Response.ok(responseData).build();
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseWithJsonBodyBuilder.withInternalError();
        }
    }

    @GET
    @Path("file-shares")
    public JsonArray userSharedFiles(@QueryParam("userId") String userId) {
        return driveService.getSharedFilesForUser(userId);
    }

}
