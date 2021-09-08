package com.fisnikz.snapdrive.drive.boundary;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import java.io.InputStream;
import java.util.Arrays;

public class FileUploadForm {

    @FormParam("data")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream fileData;
}