package com.fisnikz.snapdrive.api.drive.control;

import com.fisnikz.snapdrive.api.drive.entity.DriveFile;
import com.fisnikz.snapdrive.api.drive.entity.FilePermission;
import com.fisnikz.snapdrive.api.drive.entity.GoogleStorageQuota;
import com.fisnikz.snapdrive.api.users.control.GoogleAuthService;
import com.fisnikz.snapdrive.logging.Logged;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.WebApplicationException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
@Logged
public class GoogleDriveService {

    @Inject
    GoogleAuthService googleAuthService;

    private Drive getDriveService() throws IOException {
        //TODO handle refreshToken when access token expires in google oauth
        Credential credential =
                new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(googleAuthService.getAccessToken());
        HttpRequestFactory requestFactory = new ApacheHttpTransport().createRequestFactory(credential);

        try {
            return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), new GsonFactory(), requestFactory.getInitializer())
                    .setApplicationName("SNAP-D")
                    .build();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new WebApplicationException(408);
        }
    }

    public String uploadFile(java.io.File toUploadFile) throws IOException {
        Drive googleDrive = getDriveService();

        File driveFile = new File();
        driveFile.setName("snapdrive." + UUID.randomUUID().toString() + ".zip");
        FileContent mediaContent = new FileContent("application/zip", toUploadFile);

        File uploadedFileResult = googleDrive.files().create(driveFile, mediaContent)
                .setFields("id")
                .execute();

        return uploadedFileResult.getId();
    }

    public void updateFile(String fileId, java.io.File toUploadFile) throws IOException {
        File driveFile = new File();
        driveFile.setName("snapdrive." + UUID.randomUUID().toString() + ".zip");
        FileContent mediaContent = new FileContent("application/zip", toUploadFile);

        getDriveService().files().update(fileId, driveFile, mediaContent)
                .setFields("id")
                .execute();
    }

    public FilePermission addReaderPermissionToFile(String email, String fileId) throws IOException {
        Permission newUserPermission = new Permission()
                .setType("user")
                .setRole("reader")
                .setEmailAddress(email);

        getDriveService().permissions().create(fileId, newUserPermission)
                .setFields("id")
                .execute();

        return new FilePermission(email, "reader");
    }

    public List<DriveFile> getFiles(boolean sharedWithMeOnly) throws IOException {
        System.out.println("sharedWithMeOnly = " + sharedWithMeOnly);
//        String queryParams = "mimeType='application/zip' and name contains 'snapdrive' and 'avdia650@gmail.com' in writers";
        String queryParams = "mimeType='application/zip' and name contains 'snapdrive' and visibility = 'limited'";
        if (sharedWithMeOnly) {
            queryParams += " and sharedWithMe";
        }

        System.out.println("queryParams = " + queryParams);

        FileList result = getDriveService().files().list()
                .setPageSize(10)
                .setQ(queryParams)
                .setFields("nextPageToken,files(id,name,size,permissions(emailAddress,role),sharingUser(emailAddress),createdTime)")
                .execute();

        List<File> files = result.getFiles();
        System.out.println("result.getNextPageToken() " + result.getNextPageToken());

        List<DriveFile> driveFiles = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")
                    .withZone(ZoneId.systemDefault());
            for (File file : files) {
                System.out.println("googleId: " + file.getId());
                String formattedDate = dateFormatter.format(Instant.ofEpochMilli(file.getCreatedTime().getValue()));
                if (sharedWithMeOnly) {
                    driveFiles.add(new DriveFile(file.getId(), file.getSize(), formattedDate, file.getSharingUser().getEmailAddress()));
                } else {
                    List<FilePermission> permissions = new ArrayList<>();
                    if (file.getPermissions() != null) {
                        file.getPermissions().forEach(p -> {
                            permissions.add(new FilePermission(p.getEmailAddress(), p.getRole()));
                        });
                    }
                    driveFiles.add(new DriveFile(file.getId(), file.getSize(), formattedDate, permissions));
                }
            }
        }
        return driveFiles;
    }

    public void deleteFile(String fileId) throws IOException {
        getDriveService().files().delete(fileId).execute();
    }

    public GoogleStorageQuota getStorageQuota() throws IOException {
        About about = getDriveService()
                .about()
                .get()
                .setFields("storageQuota")
                .execute();

        return new GoogleStorageQuota(about.getStorageQuota().getLimit(), about.getStorageQuota().getUsageInDrive(), about.getStorageQuota().getUsage());
    }

    public void downloadFile(String fileId, String writeToFilePath) throws IOException {
        Drive driveService = getDriveService();
        OutputStream outputStream = new FileOutputStream(new java.io.File(writeToFilePath));
        driveService.files()
                .get(fileId)
                .executeMediaAndDownloadTo(outputStream);

        outputStream.flush();
        outputStream.close();
    }

    public DriveFile getFile(String fileId) throws IOException {
        File file = getDriveService().files().get(fileId).setFields("id,name,size,permissions(emailAddress,role),sharingUser(emailAddress),createdTime").execute();
        List<FilePermission> permissions = new ArrayList<>();
        if (file.getPermissions() != null) {
            file.getPermissions().forEach(p -> {
                permissions.add(new FilePermission(p.getEmailAddress(), p.getRole()));
            });
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        String formattedDate = dateFormatter.format(Instant.ofEpochMilli(file.getCreatedTime().getValue()));

        return new DriveFile(file.getId(), file.getSize(), formattedDate, permissions);
    }
}
