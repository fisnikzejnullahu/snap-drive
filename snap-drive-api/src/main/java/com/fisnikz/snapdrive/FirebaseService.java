package com.fisnikz.snapdrive;

import com.fisnikz.snapdrive.drive.entity.SharedFileMetadata;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.entity.DriveFile;
import com.fisnikz.snapdrive.users.entity.User;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
@Logged
public class FirebaseService {

    private static final String STORAGE_BUCKET = "test-test-955b1.appspot.com";
    private static final String PROJECT_ID = "test-test-955b1";
    private static final String CREDENTIALS_FILE_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snap-drive-api\\serviceAccount.json";
    private StorageOptions storageOptions;

    @PostConstruct
    public void init() throws IOException {
        initStorage();
        initDb();
    }

    private void initDb() throws IOException {
        InputStream serviceAccount = new FileInputStream(CREDENTIALS_FILE_PATH);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();

        FirebaseApp.initializeApp(options);
    }

    private void initStorage() throws IOException {
        this.storageOptions = StorageOptions.newBuilder()
                .setProjectId(PROJECT_ID)
                .setCredentials(GoogleCredentials.fromStream(new
                        FileInputStream(CREDENTIALS_FILE_PATH))).build();
    }

    public ApiFuture<WriteResult> addOrUpdateUser(User user) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        if (user.getFiles() == null) {
            user.setFiles(new ArrayList<>());
        }

        ApiFuture<WriteResult> future = db.collection("users").document(user.getId()).set(user);
        WriteResult writeResult = future.get();
        return future;
    }

    public DriveFile uploadToBucket(String userId, InputStream fileBytes, long createdAt) throws IOException {
        Storage storage = storageOptions.getService();

        String fileName = UUID.randomUUID().toString() + ".zip";
        BlobId blobId = BlobId.of(STORAGE_BUCKET, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        byte[] streamBytes = fileBytes.readAllBytes();
        storage.create(blobInfo, streamBytes);
        //make public url
        storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        System.out.println(blobId.getBucket());
        System.out.println(blobId.getGeneration());
        System.out.println(blobId.toGsUtilUri());

        System.out.println("Link: https://storage.googleapis.com/" + blobId.getBucket() + "/" + fileName);

        DriveFile driveFile = new DriveFile(UUID.randomUUID().toString(), "https://storage.googleapis.com/" + blobId.getBucket() + "/" + fileName,
                streamBytes.length, createdAt, new ArrayList<>());

        addNewFileToUser(userId, driveFile);
//        return "https://storage.googleapis.com/" + blobId.getBucket() + "/" + fileName;
        return driveFile;
    }

    public ApiFuture<QuerySnapshot> findUserByUsername(String username) {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection("users")
                .whereEqualTo("username", username).get();
    }

    public ApiFuture<DocumentSnapshot> findUserById(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection("users").document(userId).get();
    }

    public void deleteFileShares(ArrayList<String> shareRelationIds) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        WriteBatch batch = db.batch();

        shareRelationIds.stream()
                .map(db.collection("shared-files")::document)
                .forEach(batch::delete);
        ApiFuture<List<WriteResult>> future = batch.commit();

        for (WriteResult result : future.get()) {
            System.out.println("Update time : " + result.getUpdateTime());
        }
    }

    public boolean deleteFile(String fileLink) {
        System.out.println("fileLink = " + fileLink);
        var fileName = fileLink.substring(fileLink.lastIndexOf("/") + 1, fileLink.lastIndexOf(".zip"));

        Storage storage = storageOptions.getService();
        //first deleteFileFromStorage because you cant directly update bytes of blob
        BlobId blob = BlobId.of(STORAGE_BUCKET, fileName + ".zip");
        return storage.delete(blob);
    }

    public void addNewFileToUser(String userId, DriveFile driveFile) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference userRef = db.collection("users").document(userId);
            User user = userRef.get().get().toObject(User.class);
            user.getFiles().add(driveFile);
            ApiFuture<WriteResult> files = userRef.update("files", user.getFiles());
            files.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public boolean removeFileFromUserAndDeleteFromStorage(String userId, String fileId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            DocumentReference userRef = db.collection("users").document(userId);
            User user = userRef.get().get().toObject(User.class);
            DriveFile theDriveFile = user.getFiles()
                    .stream()
                    .filter(file -> file.getId().equals(fileId))
                    .findFirst()
                    .get();

            deleteFileShares(theDriveFile.getFileSharesRelationIds());
            user.getFiles().remove(theDriveFile);
            ApiFuture<WriteResult> files = userRef.update("files", user.getFiles());
            files.get();

            return deleteFile(theDriveFile.getLink());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String addNewSharedFile(SharedFileMetadata sharedFileMetadata) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        var shareRelationId = UUID.randomUUID().toString();
        ApiFuture<WriteResult> future = db.collection("shared-files").document(shareRelationId).set(sharedFileMetadata);
        WriteResult writeResult = future.get();
        System.out.println("shareRelationId = " + shareRelationId);
        return shareRelationId;
    }

    public ApiFuture<QuerySnapshot> findUserSharedFiles(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection("shared-files")
                .whereEqualTo("recipientId", userId).get();
    }
}
