package com.fisnikz.snapdrive.api.drive.boundary;

import com.fisnikz.snapdrive.api.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.api.drive.control.DriveService;
import com.fisnikz.snapdrive.api.drive.entity.*;
import com.fisnikz.snapdrive.api.users.entity.LoggedInUserInfo;
import com.fisnikz.snapdrive.logging.Logged;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

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
    @Inject
    LoggedInUserInfo loggedInUserInfo;

    @POST
    @Path("unlock")
    public JsonObject unlockFiles(JsonObject masterPasswordObj) {
        return driveService.unlockFiles(masterPasswordObj.getString("masterPassword"));
    }

    @GET
    public JsonObject getFiles(@Context UriInfo uriInfo) {
        boolean sharedWithMeOnly = uriInfo.getQueryParameters().containsKey("sharedWithMeOnly");
        return driveService.getAllFiles(sharedWithMeOnly);
    }

    @GET
    @Path("{fileId}")
    public JsonObject getFile(@PathParam("fileId") String fileId) {
        return driveService.getFile(fileId);
    }

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@MultipartForm FileUploadForm form) throws IOException {
//        return driveService.upload(form);
        String fileId = driveService.uploadToGoogleDrive(form);
        return Response.created(URI.create("/drive/" + fileId)).build();
    }

    @GET
    @Path("download")
    public Response downloadFile(@QueryParam("fileId") String fileId, @Context UriInfo uriInfo) throws IOException {
        boolean sharedFile = uriInfo.getQueryParameters().containsKey("sharedFile");
        DownloadedFileMetadata downloadedFileMetadata = driveService.downloadDecryptedFile(fileId, sharedFile);

        return Response.ok(downloadedFileMetadata.getBytes(), downloadedFileMetadata.getMimeType())
                .header("Content-disposition", "attachment; filename=" + downloadedFileMetadata.getFileName())
                .build();
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("id") String fileId) {
        driveService.deleteFile(fileId);
    }

    @GET
    @Path("size")
    public JsonObject sizeInCloud() {
        return driveService.calculateTotalStorageSize();
    }

    @POST
    @Path("{id}/file-shares")
    public Response shareFile(@PathParam("id") String fileId, @QueryParam("recipientEmail") String recipientEmail) {
        if (recipientEmail
                .toLowerCase()
                .equals(loggedInUserInfo.getUser().getEmail().toLowerCase())) {
            return ResponseWithJsonBodyBuilder.withInformation(400, "You cannot share your own files with yourself!");
        }

        try {
            FilePermission data = driveService.shareFile(fileId, recipientEmail);
            return Response.ok(data).build();
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseWithJsonBodyBuilder.withInternalError();
    }

    @GET
    @Path("file-shares")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DriveFile> userSharedFiles() {
        return driveService.getUserSharedFiles();
    }

}
