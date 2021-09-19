package com.fisnikz.snapdrive.users.control;

import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.entity.User;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
@Logged
public class FirebaseService {

    private static final String CREDENTIALS_FILE_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snap-drive-api\\serviceAccount.json";

    @PostConstruct
    public void init() throws IOException {
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

    public ApiFuture<WriteResult> addOrUpdateUser(User user) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> future = db.collection("users").document(user.getId()).set(user);
        WriteResult writeResult = future.get();
        return future;
    }

    public ApiFuture<QuerySnapshot> findUserByEmail(String email) {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection("users")
                .whereEqualTo("email", email).get();
    }

    public ApiFuture<DocumentSnapshot> findUserById(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection("users").document(userId).get();
    }

}
