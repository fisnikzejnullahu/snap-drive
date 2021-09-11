package com.fisnikz.snapdrive.api.drive.control;

import com.fisnikz.snapdrive.api.MyResponseExceptionMapper;
import com.fisnikz.snapdrive.api.drive.entity.FileShare;
import com.fisnikz.snapdrive.api.drive.entity.FileUploadForm;
import com.fisnikz.snapdrive.api.drive.entity.DriveFile;
import com.fisnikz.snapdrive.logging.Logged;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Fisnik Zejnullahu
 */
@Path("/drive")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(baseUri = "http://localhost:8080")
@RegisterProvider(MyResponseExceptionMapper.class)
@Logged
public interface DriveResourceClient {

    @POST
    @Path("upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    DriveFile upload(@QueryParam("userId") String userId, @MultipartForm FileUploadForm data);

    @PUT
    @Path("{fileId}")
    DriveFile update(@PathParam("fileId") String fileId, DriveFile driveFile);

    @GET
    List<DriveFile> getFiles(@QueryParam("userId") String userId);

    @GET
    @Path("{id}")
    DriveFile getFile(@PathParam("id") String fileId);

    @DELETE
    @Path("{id}")
    JsonObject delete(@PathParam("id") String fileId);

    @POST
    @Path("file-shares")
    FileShare shareFile(FileShare shareFileRequest);

    @GET
    @Path("file-shares")
    JsonArray getSharedFilesToUser(@QueryParam("userId") String userId);

    @DELETE
    @Path("file-shares")
    Response deleteFileShare(@QueryParam("userId") String userId);

    @POST
    Response create(DriveFile driveFile);
}
