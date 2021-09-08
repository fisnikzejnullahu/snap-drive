package com.fisnikz.snapdrive.api.drive.entity;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * @author Fisnik Zejnullahu
 */
public class FileUploadForm {

    @FormParam("name")
    @PartType(MediaType.TEXT_PLAIN)
    public String fileName;

    @FormParam("data")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream fileData;


}
