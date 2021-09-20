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
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

    @Inject
    @ConfigProperty(name = "FIREBASE.SERVICE.ACCOUNT.FILE.PATH", defaultValue = "../serviceAccount.json")
    String serviceAccountPath;

    @PostConstruct
    public void init() throws IOException {
        initDb();
    }

    private void initDb() throws IOException {
        InputStream serviceAccount = new FileInputStream(serviceAccountPath);
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
