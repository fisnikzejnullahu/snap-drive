package com.fisnikz.snapdrive.drive.control;

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
import java.io.StringReader;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
@Logged
@Transactional
public class DriveService {

    @Inject
    UsersService usersService;
    @Inject
    Jsonb jsonb;

    public JsonObject shareFile(ShareFileRequest shareFileRequest) {
        User ownerUser = usersService.getUserByIdOrThrow404(shareFileRequest.getOwnerId());
        User recipientUser = usersService.getUserByEmailOrThrow404(shareFileRequest.getRecipientEmail());

        System.out.println(jsonb.toJson(shareFileRequest));

        FileShare fileShare = new FileShare(shareFileRequest.getShareId(), shareFileRequest.getDriveFileId(), ownerUser, recipientUser);
        System.out.println("fileShare = " + fileShare.recipientUser.email);
        fileShare.persist();

        return Json.createObjectBuilder()
                .add("driveFileId", fileShare.driveFileId)
                .add("recipientUsername", fileShare.recipientUser.email)
                .add("sharedAt", fileShare.sharedAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")))
                .build();
    }

//    public JsonObject shareFile(FileShare fileShare) {
//        User ownerUser = usersService.getUserByIdOrThrow404(f.getOwnerId());
//        User recipientUser = usersService.getUserByEmailOrThrow404(shareFileRequest.getRecipientUsername());
//
//        FileShare fileShare = new FileShare(shareFileRequest.getDriveFileId(), ownerUser, recipientUser);
//        System.out.println("fileShare = " + fileShare.recipientUser.email);
//        fileShare.persist();
//
//        return Json.createObjectBuilder()
//                .add("driveFileId", fileShare.driveFileId)
//                .add("recipientUsername", fileShare.recipientUser.email)
//                .add("sharedAt", fileShare.sharedAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")))
//                .build();
//    }

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
                            .add("sharedBy", theShare.ownerUser.email)
                            .build();
                }).forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    public void deleteFileShares(String driveFileId) {
        System.out.println("driveFileId = " + driveFileId);
        FileShare.delete("driveFileId", driveFileId);
    }

//    public List<DriveFile> getSentFilesOfUser(String userId) {
//        usersService.getUserByIdOrThrow404(userId);
//
//        return FileShare.find("file.user.id", userId)
//                .stream()
//                .map(item -> ((FileShare) item).file)
//                .collect(Collectors.toList());
//    }


}
