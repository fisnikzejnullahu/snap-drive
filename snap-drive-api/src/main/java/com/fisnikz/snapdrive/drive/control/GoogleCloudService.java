package com.fisnikz.snapdrive.drive.control;

import com.fisnikz.snapdrive.drive.entity.DriveFile;
import com.fisnikz.snapdrive.logging.Logged;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
@Logged
public class GoogleCloudService {

    private static final String STORAGE_BUCKET = "test-test-955b1.appspot.com";
    private static final String PROJECT_ID = "test-test-955b1";
    private static final String CREDENTIALS_FILE_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snap-drive-api\\serviceAccount.json";

    private StorageOptions storageOptions;

    @PostConstruct
    public void init() throws IOException {
        initStorage();
    }

    private void initStorage() throws IOException {
        this.storageOptions = StorageOptions.newBuilder()
                .setProjectId(PROJECT_ID)
                .setCredentials(GoogleCredentials.fromStream(new
                        FileInputStream(CREDENTIALS_FILE_PATH))).build();
    }

    public DriveFile uploadToBucket(InputStream fileBytes, long createdAt) throws IOException {
        Storage storage = storageOptions.getService();

        String fileName = UUID.randomUUID().toString() + ".zip";
        BlobId blobId = BlobId.of(STORAGE_BUCKET, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        byte[] streamBytes = fileBytes.readAllBytes();
        System.out.println("UPLOADING...");

        storage.create(blobInfo, streamBytes);
        //make public url
        storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        DriveFile driveFile = new DriveFile(UUID.randomUUID().toString(), "https://storage.googleapis.com/" + blobId.getBucket() + "/" + fileName,
                streamBytes.length, createdAt);

        return driveFile;
    }

    public boolean deleteFile(String fileLink) {
        System.out.println("fileLink = " + fileLink);
        var fileName = fileLink.substring(fileLink.lastIndexOf("/") + 1, fileLink.lastIndexOf(".zip"));

        Storage storage = storageOptions.getService();
        //first deleteFileFromStorage because you cant directly update bytes of blob
        BlobId blob = BlobId.of(STORAGE_BUCKET, fileName + ".zip");
        return storage.delete(blob);
    }
}
