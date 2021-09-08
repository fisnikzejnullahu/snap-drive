package com.fisnikz.snapdrive.drive.control;

import com.fisnikz.snapdrive.FirebaseService;
import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.drive.entity.SharedFileMetadata;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.control.UsersService;
import com.fisnikz.snapdrive.users.entity.DriveFile;
import com.fisnikz.snapdrive.users.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@ApplicationScoped
@Logged
public class DriveService {

    @Inject
    FirebaseService firebaseService;

    @Inject
    UsersService usersService;

    // returns fileUrl
    public DriveFile upload(String userId, InputStream fileBytes) throws IOException {
        return firebaseService.uploadToBucket(userId, fileBytes, Instant.now().toEpochMilli());
    }

    public DriveFile updateFile(String userId, String fileId, InputStream newFileBytes) throws IOException {
        //some async way
        try {
            User user = firebaseService.findUserById(userId).get().toObject(User.class);
            DriveFile originalDriveFile =
                    user.getFiles()
                            .stream()
                            .filter(file -> file.getId().equals(fileId))
                            .findFirst()
                            .get();

            DriveFile newDriveFile = firebaseService.uploadToBucket(userId, newFileBytes, originalDriveFile.getCreatedAt());
            boolean deleteFileFromStorage = deleteFileFromStorage(originalDriveFile.getLink());
            if (deleteFileFromStorage) {
                originalDriveFile.setLink(newDriveFile.getLink());
                originalDriveFile.setSize(newDriveFile.getSize());
                firebaseService.addOrUpdateUser(user);
                return newDriveFile;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DriveFile getFile(String fileId) {
        return null;
    }

    public boolean deleteFileFromStorage(String fileLink) {
        return firebaseService.deleteFile(fileLink);
    }

    public boolean deleteFilePermanent(String userId, String fileId) {
        return firebaseService.removeFileFromUserAndDeleteFromStorage(userId, fileId);
    }

    public List<DriveFile> getAllFiles(String userId) throws ExecutionException, InterruptedException {
        return firebaseService.findUserById(userId).get().toObject(User.class).getFiles();
    }

    public boolean shareFile(SharedFileMetadata sharedFileMetadata) {
        try {
            User user = usersService.findUser(sharedFileMetadata.getOwnerUsername());
            String addedSharedDocumentId = firebaseService.addNewSharedFile(sharedFileMetadata);

            DriveFile theDriveFile =
                    user.getFiles()
                            .stream()
                            .filter(file -> file.getId().equals(sharedFileMetadata.getFileId()))
                            .findFirst()
                            .get();

            theDriveFile.getFileSharesRelationIds().add(addedSharedDocumentId);
            firebaseService.addOrUpdateUser(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInternalError());
        }
    }

    public List<SharedFileMetadata> getUserSharedFiles(String userId) throws ExecutionException, InterruptedException {
        return firebaseService.findUserSharedFiles(userId)
                .get()
                .getDocuments()
                .stream()
                .map(queryDocumentSnapshot -> queryDocumentSnapshot.toObject(SharedFileMetadata.class))
                .collect(Collectors.toList());
    }
}
