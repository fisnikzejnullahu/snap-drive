package com.fisnikz.snapdrive.drive.control;

import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.drive.entity.ShareFileRequest;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.control.UsersService;
import com.fisnikz.snapdrive.drive.entity.DriveFile;
import com.fisnikz.snapdrive.drive.entity.FileShare;
import com.fisnikz.snapdrive.users.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.*;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Logged
@Transactional
public class DriveService {

    @Inject
    GoogleCloudService cloudService;

    @Inject
    UsersService usersService;

    // returns fileUrl
    public DriveFile upload(String userId, InputStream fileBytes) throws IOException {
        User user = usersService.getUserByIdOrThrow404(userId);

        DriveFile driveFile = cloudService.uploadToBucket(fileBytes, Instant.now().toEpochMilli());
        driveFile.user = user;
        driveFile.persist();
        return driveFile;
    }

    public DriveFile updateFile(String userId, String fileId, InputStream newFileBytes) throws IOException {
        usersService.getUserByIdOrThrow404(userId);
        DriveFile originalDriveFile = getFileByIdOrThrow404(fileId);

        DriveFile newDriveFile = cloudService.uploadToBucket(newFileBytes, originalDriveFile.createdAt);
        boolean deleteFileFromStorage = deleteFileFromStorage(originalDriveFile.link);

        if (deleteFileFromStorage) {
            originalDriveFile.link = newDriveFile.link;
            originalDriveFile.size = newDriveFile.size;
            originalDriveFile.persist();
            return newDriveFile;
        }
        return null;
    }

    public void shareFile(ShareFileRequest shareFileRequest) {
        User recipientUser = usersService.getUserByUsernameOrThrow404(shareFileRequest.getRecipientUsername());

        DriveFile theDriveFile = DriveFile.findById(shareFileRequest.getFileId());
        if (theDriveFile == null) {
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "File not found!"));
        }

        FileShare fileShare = new FileShare(theDriveFile, recipientUser);
        fileShare.persist();
    }

    public JsonArray getSharedFilesForUser(String userId) {
        System.out.println("userId = " + userId);
        usersService.getUserByIdOrThrow404(userId);

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        FileShare.list("recipientUser.id", userId)
                .stream()
                .map(share -> {
                    FileShare theShare = ((FileShare) share);
                    JsonObject theShareJson = Json.createReader(new StringReader(JsonbBuilder.create().toJson(theShare))).readObject();

                    return Json.createObjectBuilder(theShareJson)
                            .add("sharedBy", theShare.file.user.username)
                            .build();
                }).forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    public List<DriveFile> getSentFilesOfUser(String userId) {
        usersService.getUserByIdOrThrow404(userId);

        return FileShare.find("file.user.id", userId)
                .stream()
                .map(item -> ((FileShare) item).file)
                .collect(Collectors.toList());
    }

    public DriveFile getFileByIdOrThrow404(String id) {
        return (DriveFile) DriveFile.findByIdOptional(id).orElseThrow(() -> new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "File was not found!")));
    }

    public boolean deleteFileFromStorage(String fileLink) {
        return cloudService.deleteFile(fileLink);
    }

    public List<DriveFile> getAllFilesOfUser(String userId) {
        return DriveFile.list("user.id", userId);
    }

    public boolean deleteFile(String fileId) {
        System.out.println("fileId = " + fileId);
        DriveFile driveFile = getFileByIdOrThrow404(fileId);
        boolean deleted = DriveFile.deleteById(fileId);
        if (deleted) {
            deleteFileFromStorage(driveFile.link);
        }
        return deleted;
    }
}
