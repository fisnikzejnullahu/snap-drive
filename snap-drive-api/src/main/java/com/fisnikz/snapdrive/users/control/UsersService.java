package com.fisnikz.snapdrive.users.control;

import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.entity.CreateUserRequest;
import com.fisnikz.snapdrive.users.entity.User;
import com.fisnikz.snapdrive.users.entity.UserLoginRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
@Transactional
@Logged
public class UsersService {

    public String create(CreateUserRequest createUserRequest) {
        if (User.find("username", createUserRequest.getUsername()).count() != 0){
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(409, "User with that name exists!"));
        }

        var salt = generateSalt();
        String hashedPassword = sha512(createUserRequest.getPassword().getBytes(), salt);

        User user = new User(createUserRequest.getUsername(), hashedPassword, encodeToBase64(salt),
                createUserRequest.getDerivativeIterations(),
                createUserRequest.getDerivativeSalt(),
                createUserRequest.getPrivateKey(),
                createUserRequest.getPublicKey(),
                createUserRequest.getNonce());

        user.persist();
        return user.id;
    }

    public Response updatePassword(String userId, String newPassword) {
        User user = getUserByIdOrThrow404(userId);
        if (user == null) {
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User not found!"));
        }

        var salt = generateSalt();
        String hashedPassword = sha512(newPassword.getBytes(), salt);
        user.passwordSalt = encodeToBase64(salt);
        user.hashedPassword = hashedPassword;

        return Response.noContent().build();
    }

    public User login(UserLoginRequest request) {
        User user = getUserByUsernameOrThrow404(request.getUsername());
        System.out.println(request);
        boolean passwordSame = checkPasswordsEqualHash(request.getPassword(), user.passwordSalt, user.hashedPassword);
        if (!passwordSame) {
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(401, "Incorrect password!"));
        }
        return user;
    }

    public boolean updateUser(String userId, User newUserInfo) {
        User user = getUserByIdOrThrow404(userId);
        user.username = newUserInfo.username;
        user.privateKey = newUserInfo.privateKey;
        user.publicKey = newUserInfo.publicKey;
        user.derivativeSalt = newUserInfo.derivativeSalt;
        user.derivativeIterations = newUserInfo.derivativeIterations;
        user.nonce = newUserInfo.nonce;

        return true;
    }

    public JsonObject getUserWithGivenFields(User user, String requestedFields) {
        JsonObjectBuilder responseData = Json.createObjectBuilder()
                .add("username", user.username);

        for (String field : requestedFields.split(",")) {
            if (field.equals("userId")) {
                responseData.add("userId", user.id);
            }
            if (field.equals("username")) {
                responseData.add("username", user.username);
            }
            if (field.equals("public_key")) {
                responseData.add("publicKey", user.publicKey);
            }
        }

        return responseData.build();
    }

    public User getUserByIdOrThrow404(String id) {
        return (User) User.findByIdOptional(id).orElseThrow(() -> new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User with given username was not found!")));
    }

    public User getUserByUsernameOrThrow404(String username) {
        return (User) User.find("username", username).singleResultOptional()
                .orElseThrow(() -> new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User with given username was not found!")));
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
}
