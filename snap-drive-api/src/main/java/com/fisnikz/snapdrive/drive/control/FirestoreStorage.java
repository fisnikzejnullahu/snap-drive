package com.fisnikz.snapdrive.drive.control;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;


/**
 * @author Fisnik Zejnullahu
 */
public class FirestoreStorage {

    public static void main(String... args) {
        try {
            new FirestoreStorage().go();
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void go() throws IOException, ExecutionException, InterruptedException {


// Use a service account
        InputStream serviceAccount = new FileInputStream("serviceAccount.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setStorageBucket("test-test-955b1.appspot.com")
                .build();
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);

        Firestore db = FirestoreClient.getFirestore();

        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setProjectId("test-test-955b1")
                .setCredentials(GoogleCredentials.fromStream(new
                        FileInputStream("serviceAccount.json"))).build();

        Storage storage = storageOptions.getService();
        BlobId blobId = BlobId.of("test-test-955b1.appspot.com", "publike4.json");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(Path.of("serviceAccount.json")));
        storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        System.out.println(blobId.getBucket());
        System.out.println(blobId.getGeneration());
        System.out.println(blobId.toGsUtilUri());

        System.out.println("Link: https://storage.googleapis.com/" + blobId.getBucket() + "/publike4.json");
//        System.out.println(
//                "File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);



//        DocumentReference docRef = db.collection("users").document("alovelace");
//// Add document data  with id "alovelace" using a hashmap
//        Map<String, Object> data = new HashMap<>();
//        data.put("first", "Ada");
//        data.put("last", "Lovelace");
//        data.put("born", 1815);
////asynchronously write data
//        ApiFuture<WriteResult> result = docRef.set(data);
//// ...
//// result.get() blocks on response
//        System.out.println("Update time : " + result.get().getUpdateTime());

    }
}
