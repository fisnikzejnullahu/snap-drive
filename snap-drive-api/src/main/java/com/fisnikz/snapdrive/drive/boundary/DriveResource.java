package com.fisnikz.snapdrive.drive.boundary;

import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.drive.control.DriveService;
import com.fisnikz.snapdrive.drive.entity.SharedFileMetadata;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.entity.DriveFile;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    @POST
    @Path("upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    public void upload(@Suspended AsyncResponse response,
                       @QueryParam("userId") String userId,
                       @MultipartForm FileUploadForm form) {

        CompletableFuture.supplyAsync(() -> {

            try {
                return driveService.upload(userId, form.fileData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).thenAccept(response::resume);
    }

    @PUT
    @Path("{fileId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public DriveFile update(@MultipartForm FileUploadForm form,
                            @PathParam("fileId") String fileId,
                            @QueryParam("userId") String userId) {
        try {
            return driveService.updateFile(userId, fileId, form.fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public DriveFile getFile(@PathParam("id") String fileId) {
        return driveService.getFile(fileId);
    }

    @DELETE
    @Path("{fileId}")
    public JsonObject delete(@PathParam("fileId") String fileId,
                             @QueryParam("userId") String userId) {
        boolean deleteFileFromStorage = driveService.deleteFilePermanent(userId ,fileId);

        return Json.createObjectBuilder()
                .add("id", fileId)
                .add("deleted", deleteFileFromStorage)
                .build();
    }

    @GET
    public List<DriveFile> allUserFiles(@QueryParam("userId") String userId) {
        try {
            return driveService.getAllFiles(userId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @POST
    @Path("file-shares")
    public Response shareFile(SharedFileMetadata sharedFileMetadata) {
        boolean shared = driveService.shareFile(sharedFileMetadata);
        JsonObject responseData = Json.createObjectBuilder()
                .add("shared", shared)
                .build();
        return Response.ok(responseData).build();
    }

    @GET
    @Path("file-shares")
    public Response userSharedFiles(@QueryParam("userId") String userId) {
        try {
            return Response.ok(driveService.getUserSharedFiles(userId)).build();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseWithJsonBodyBuilder.withInternalError();
    }

}
