package com.fisnikz.snapdrive.api.drive.control;

import com.fisnikz.snapdrive.api.ClientResponseExceptionMapper;
import com.fisnikz.snapdrive.api.drive.entity.FilePermission;
import com.fisnikz.snapdrive.logging.Logged;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Fisnik Zejnullahu
 */
@Path("drive/shares")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(baseUri = "http://localhost:8080")
@RegisterProvider(ClientResponseExceptionMapper.class)
@Logged
public interface DriveSharesResourceClient {

    @GET
    JsonArray getSharedFilesToUser(@QueryParam("userId") String userId);

    @POST
    FilePermission shareFile(FilePermission shareFileRequest);

    @DELETE
    JsonObject deleteFileShares(@QueryParam("fileId") String fileId);

}
