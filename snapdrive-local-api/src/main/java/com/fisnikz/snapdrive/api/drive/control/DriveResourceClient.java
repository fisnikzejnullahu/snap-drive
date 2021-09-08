package com.fisnikz.snapdrive.api.drive.control;

import com.fisnikz.snapdrive.api.MyResponseExceptionMapper;
import com.fisnikz.snapdrive.api.drive.entity.FileUploadForm;
import com.fisnikz.snapdrive.api.drive.entity.SharedFileMetadata;
import com.fisnikz.snapdrive.api.users.entity.DriveFile;
import com.fisnikz.snapdrive.logging.Logged;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Fisnik Zejnullahu
 */
@Path("/drive")
@RegisterRestClient(baseUri = "http://localhost:8080")
@RegisterProvider(MyResponseExceptionMapper.class)
@Logged
public interface DriveResourceClient {

    @POST
    @Path("upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    DriveFile upload(@QueryParam("userId") String userId, @MultipartForm FileUploadForm data);

    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("{fileId}")
    DriveFile update(@PathParam("fileId") String fileId, @QueryParam("userId") String userId, @MultipartForm FileUploadForm data);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<DriveFile> allFiles(@QueryParam("userId") String userId);

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    DriveFile getFile(@PathParam("id") String fileId);

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    JsonObject delete(@PathParam("id") String fileId, @QueryParam("userId") String userId);

    @POST
    @Path("file-shares")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    JsonObject shareFile(SharedFileMetadata sharedFileMetadata);

    @GET
    @Path("file-shares")
    @Produces(MediaType.APPLICATION_JSON)
    List<SharedFileMetadata> userSharedFiles(@QueryParam("userId") String userId);

    @DELETE
    @Path("file-shares")
    Response deleteFileShare(@QueryParam("userId") String userId);

}
