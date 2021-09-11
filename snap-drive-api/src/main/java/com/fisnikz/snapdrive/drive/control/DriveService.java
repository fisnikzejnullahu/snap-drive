package com.fisnikz.snapdrive.drive.control;

import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.drive.entity.DriveFile;
import com.fisnikz.snapdrive.drive.entity.FileShare;
import com.fisnikz.snapdrive.drive.entity.ShareFileRequest;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.control.UsersService;
import com.fisnikz.snapdrive.users.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@Logged
@Transactional
public class DriveService {

    @Inject
    UsersService usersService;
    @Inject
    Jsonb jsonb;

    public JsonArray getFilesOfUser(String userId) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        DriveFile.list("user.id", userId)
                .stream()
                .map(file -> fileAsJson(((DriveFile) file)))
                .forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    public JsonObject getFile(String id) {
        return fileAsJson(getFileByIdOrThrow404(id));
    }

    private JsonObject fileAsJson(DriveFile file) {
        JsonObject obj = Json.createReader(new StringReader(jsonb.toJson(file))).readObject();
        JsonArrayBuilder ab = Json.createArrayBuilder();

        file.fileShares.forEach(fileShare -> {
            System.out.println(fileShare.recipientUser.username);
            ab.add(Json.createObjectBuilder()
                    .add("recipientUsername", fileShare.recipientUser.username)
                    .add("sharedAt", fileShare.sharedAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss"))));
        });

        return Json.createObjectBuilder(obj).add("shares", ab.build())
                .build();
    }

    public DriveFile getFileByIdOrThrow404(String id) {
        return (DriveFile) DriveFile.findByIdOptional(id).orElseThrow(() -> new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "File was not found!")));
    }

    public String create(DriveFile driveFile) {
        User user = usersService.getUserByIdOrThrow404(driveFile.userId);
        driveFile.id = UUID.randomUUID().toString();
        driveFile.user = user;
        driveFile.createdAt = LocalDateTime.now();
        driveFile.persist();
        return driveFile.id;
    }

    public JsonObject deleteFile(String fileId) {
        DriveFile driveFile = getFileByIdOrThrow404(fileId);
        boolean deleted = DriveFile.deleteById(fileId);

        return Json.createObjectBuilder()
                .add("id", driveFile.id)
                .add("link", driveFile.link)
                .add("deleted", deleted)
                .build();
    }

    public DriveFile update(String fileId, DriveFile newDriveFile) {
        DriveFile originalDriveFile = getFileByIdOrThrow404(fileId);

        originalDriveFile.link = newDriveFile.link;
        originalDriveFile.size = newDriveFile.size;
        originalDriveFile.persist();
        return newDriveFile;
    }

    public JsonObject shareFile(ShareFileRequest shareFileRequest) {
        User recipientUser = usersService.getUserByUsernameOrThrow404(shareFileRequest.getRecipientUsername());

        DriveFile theDriveFile = DriveFile.findById(shareFileRequest.getFileId());
        if (theDriveFile == null) {
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "File not found!"));
        }
        FileShare fileShare = new FileShare(theDriveFile, recipientUser, LocalDateTime.now());
        fileShare.persist();

        return Json.createObjectBuilder()
                .add("fileId", fileShare.file.id)
                .add("recipientUsername", fileShare.recipientUser.username)
                .add("sharedAt", fileShare.sharedAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")))
                .build();
    }

    public JsonArray getSharedFilesForUser(String userId) {
        System.out.println("userId = " + userId);
        usersService.getUserByIdOrThrow404(userId);

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        FileShare.list("recipientUser.id", userId)
                .stream()
                .map(share -> {
                    FileShare theShare = ((FileShare) share);
                    JsonObject theShareJson = Json.createReader(new StringReader(jsonb.toJson(theShare))).readObject();

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


}
