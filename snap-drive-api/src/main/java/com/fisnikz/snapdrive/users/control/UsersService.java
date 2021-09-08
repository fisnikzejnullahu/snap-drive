package com.fisnikz.snapdrive.users.control;

import com.fisnikz.snapdrive.FirebaseService;
import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.entity.CreateUserRequest;
import com.fisnikz.snapdrive.users.entity.User;
import com.fisnikz.snapdrive.users.entity.UserLoginRequest;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
@Logged
public class UsersService {

    @Inject
    FirebaseService firebaseService;

    public Response create(CreateUserRequest createUserRequest) {
        var salt = generateSalt();
        String hashedPassword = sha512(createUserRequest.getPassword().getBytes(), salt);

        User user = new User(createUserRequest.getUsername(), hashedPassword, encodeToBase64(salt),
                createUserRequest.getDerivativeIterations(),
                createUserRequest.getDerivativeSalt(),
                createUserRequest.getPrivateKey(),
                createUserRequest.getPublicKey(),
                createUserRequest.getNonce());

        try {
            QuerySnapshot query = firebaseService.findUserByUsername(createUserRequest.getUsername()).get();
            if (!query.isEmpty()) {
                throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(409, "User with that name exists!"));
            }
            ApiFuture<WriteResult> writeResultApiFuture = firebaseService.addOrUpdateUser(user);
            writeResultApiFuture.get();
            return ResponseWithJsonBodyBuilder.withInformation(200, "User created!");

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInternalError());

        }
    }

    public Response updatePassword(String userId, String newPassword) {
        try {
            DocumentSnapshot findUserSnapshot = firebaseService.findUserById(userId).get();
            if (findUserSnapshot.exists()) {
                User foundUser = findUserSnapshot.toObject(User.class);
                var salt = generateSalt();
                String hashedPassword = sha512(newPassword.getBytes(), salt);
                foundUser.setHashedPassword(hashedPassword);
                foundUser.setPasswordSalt(encodeToBase64(salt));
                firebaseService.addOrUpdateUser(foundUser);

                return Response.ok(foundUser).build();
            }
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User does not exists!"));


        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInternalError());
        }

    }

    public User login(UserLoginRequest request) {
        User user = findUser(request.getUsername());
        boolean passwordSame = checkPasswordsEqualHash(request.getPassword(), user.getPasswordSalt(), user.getHashedPassword());
        if (!passwordSame) {
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(401, "Incorrect password!"));
        }
        return user;
    }

    public boolean updateUser(String userId, User user) {
        ApiFuture<DocumentSnapshot> foundUser = firebaseService.findUserById(userId);
        try {
            if (foundUser.get().exists()) {
                ApiFuture<WriteResult> writeResultApiFuture = firebaseService.addOrUpdateUser(user);
                return true;
            } else {
                throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User does not exists!"));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInternalError());
        }
    }

    private boolean checkPasswordsEqualHash(String password, String saltBase64, String originalHashedPassword) {
        byte[] salt = decodeFromBase64(saltBase64);
        String hashedPassword = sha512(password.getBytes(), salt);
        return hashedPassword.equals(originalHashedPassword);
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[128];
        random.nextBytes(salt);
        return salt;
    }

    private String sha512(byte[] passwordToHash, byte[] salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private String encodeToBase64(byte[] array) {
        return Base64.getUrlEncoder().encodeToString(array);
    }

    private byte[] decodeFromBase64(String base64) {
        return Base64.getUrlDecoder().decode(base64);
    }

    public JsonObjectBuilder getPublicKeyOfUser(String username) {
        try {
            // ignored if exists or not...
            User user = firebaseService.findUserByUsername(username).get().getDocuments().get(0).toObject(User.class);
            return Json.createObjectBuilder()
                    .add("publicKey", user.getPublicKey());

        } catch (InterruptedException | ExecutionException e) {
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User with given username was not found!"));
        }
    }

    public JsonObject getUserWithGivenFields(String username, String fields) {
        User user = findUser(username);
        JsonObjectBuilder responseData = Json.createObjectBuilder()
                .add("username", user.getUsername());

        for (String field : fields.split(",")) {
            if (field.equals("userId")) {
                responseData.add("userId", user.getId());
            }
            if (field.equals("username")) {
                responseData.add("username", user.getUsername());
            }
            if (field.equals("public_key")) {
                responseData.add("publicKey", user.getPublicKey());
            }
        }

        return responseData.build();
    }

    public User findUser(String username) {
        try {
            QuerySnapshot snapshot = firebaseService.findUserByUsername(username).get();
            if (snapshot.isEmpty()) {
                throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User with given username was not found!"));
            }
            return snapshot.getDocuments().get(0).toObject(User.class);
        } catch (InterruptedException | ExecutionException e) {
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User with given username was not found!"));
        }
    }
}
