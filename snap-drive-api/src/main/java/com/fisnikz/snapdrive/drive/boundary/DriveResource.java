package com.fisnikz.snapdrive.drive.boundary;

import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.drive.control.DriveService;
import com.fisnikz.snapdrive.drive.entity.ShareFileRequest;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.drive.entity.DriveFile;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    public void upload(@Suspended AsyncResponse response,
                       @QueryParam("userId") String userId,
                       @MultipartForm FileUploadForm form) {

        CompletableFuture.supplyAsync(() -> {

            try {
                return driveService.upload(userId, form.fileData);
            } catch (Exception e) {
                e.printStackTrace();
                return e;
            }
        }).thenAccept(response::resume);
    }

    @PUT
    @Path("{fileId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
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
    public DriveFile getFile(@PathParam("id") String fileId) {
        return driveService.getFileByIdOrThrow404(fileId);
    }

    @DELETE
    @Path("{fileId}")
    public JsonObject delete(@PathParam("fileId") String fileId) {
        boolean deleteFileFromStorage = driveService.deleteFile(fileId);

        return Json.createObjectBuilder()
                .add("id", fileId)
                .add("deleted", deleteFileFromStorage)
                .build();
    }

    @GET
    public List<DriveFile> allUserFiles(@QueryParam("userId") String userId) {
        return driveService.getAllFilesOfUser(userId);
    }

    @POST
    @Path("file-shares")
    public Response shareFile(ShareFileRequest shareFileRequest) {
        try {
            driveService.shareFile(shareFileRequest);
            JsonObject responseData = Json.createObjectBuilder()
                    .add("shared", true)
                    .build();

            return Response.ok(responseData).build();
        }catch (Exception e) {
            return ResponseWithJsonBodyBuilder.withInternalError();
        }
    }

    @GET
    @Path("file-shares")
    public JsonArray userSharedFiles(@QueryParam("userId") String userId) {
        return driveService.getSharedFilesForUser(userId);
    }

}
