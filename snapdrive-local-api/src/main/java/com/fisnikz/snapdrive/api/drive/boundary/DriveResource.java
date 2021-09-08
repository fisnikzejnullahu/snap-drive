package com.fisnikz.snapdrive.api.drive.boundary;

import com.fisnikz.snapdrive.api.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.api.drive.control.DriveService;
import com.fisnikz.snapdrive.api.drive.entity.FileUploadForm;
import com.fisnikz.snapdrive.api.users.entity.DriveFile;
import com.fisnikz.snapdrive.api.users.entity.LoggedInUserInfo;
import com.fisnikz.snapdrive.logging.Logged;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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

    @GET
    public JsonArray getFiles() {
        return driveService.getAllFiles();
    }

    @POST
    @Path("upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject upload(@MultipartForm FileUploadForm form) throws IOException {
        return driveService.upload(form);
    }

    @GET
    @Path("download")
    public Response downloadFile(@QueryParam("fileLink") String fileLink, @QueryParam("shared") boolean shared) throws IOException {
        String filePath = driveService.downloadDecryptedFile(fileLink, shared);
        var mimeType = Files.probeContentType(Paths.get(filePath));
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        File file = new File(filePath);
        file.delete();

        return Response.ok(bytes, mimeType)
                .header("Content-disposition", "attachment; filename=" + file.getName())
                .build();
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject delete(@PathParam("id") String fileId) {
        return driveService.deleteFile(fileId);
    }

    @GET
    @Path("size")
    public JsonObject sizeInCloud() {
        return driveService.calculateTotalStorageSize();
    }

    @POST
    @Path("file-shares")
    public Response shareFile(@QueryParam("recipientUsername") String recipientUsername, DriveFile driveFile) {
        if (driveFile == null) {
            return ResponseWithJsonBodyBuilder.withInformation(400, "There is no file specified!");
        }

        if (recipientUsername
                .toLowerCase()
                .equals(loggedInUserInfo.getUser().getUsername().toLowerCase())) {
            return ResponseWithJsonBodyBuilder.withInformation(400, "You cannot share your own files with yourself!");
        }

        try {
            JsonObject data = driveService.shareFile(driveFile, recipientUsername);
            return Response.ok(data).build();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return ResponseWithJsonBodyBuilder.withInternalError();
    }

    @GET
    @Path("file-shares")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray userSharedFiles() {
        return driveService.getUserSharedFiles();
    }

}
