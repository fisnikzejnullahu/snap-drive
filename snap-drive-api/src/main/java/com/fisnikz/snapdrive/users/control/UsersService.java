package com.fisnikz.snapdrive.users.control;

import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.entity.CreateUserMasterPasswordRequest;
import com.fisnikz.snapdrive.users.entity.SignInWithGoogleRequest;
import com.fisnikz.snapdrive.users.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.Jsonb;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.time.format.DateTimeFormatter;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
@Transactional
@Logged
public class UsersService {

    @Inject
    Jsonb jsonb;

    public Response signInWithGoogle(SignInWithGoogleRequest request) {
        User user = User.find("email", request.getEmail()).firstResult();

        if (user == null) {
            user = new User(request.getGoogleId(), request.getEmail());
            user.persist();

            JsonObject responseData = Json.createObjectBuilder()
                    .add("id", user.id)
                    .add("email", user.email)
                    .add("googleId", user.googleId)
                    .add("registerAt", user.registerAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")))
                    .build();

            return Response.status(201).entity(responseData).build();
        }

        return Response.ok(user).build();
    }

    //TODO provo @embedded per tripat e paswordit te useri entity
    public User createMasterPassword(String userId, CreateUserMasterPasswordRequest createUserMasterPasswordRequest) {
        User user = getUserByIdOrThrow404(userId);

        user.privateKey = createUserMasterPasswordRequest.getPrivateKey();
        user.publicKey = createUserMasterPasswordRequest.getPublicKey();
        user.derivativeSalt = createUserMasterPasswordRequest.getDerivativeSalt();
        user.derivativeIterations = createUserMasterPasswordRequest.getDerivativeIterations();
        user.nonce = createUserMasterPasswordRequest.getNonce();

        return user;
    }

    public void updateUser(String userId, User newUserInfo) {
        User user = getUserByIdOrThrow404(userId);
        user.email = newUserInfo.email;
        user.privateKey = newUserInfo.privateKey;
        user.publicKey = newUserInfo.publicKey;
        user.derivativeSalt = newUserInfo.derivativeSalt;
        user.derivativeIterations = newUserInfo.derivativeIterations;
        user.nonce = newUserInfo.nonce;
    }

    public JsonObject getUserWithGivenFields(String email, String requestedFields) {
        User user = getUserByEmailOrThrow404(email);

        if (requestedFields.equals("*")) {
            return Json.createReader(new StringReader(jsonb.toJson(user))).readObject();
        }

        JsonObjectBuilder responseData = Json.createObjectBuilder()
                .add("email", user.email);

        for (String field : requestedFields.split(",")) {
            if (field.equals("userId")) {
                responseData.add("userId", user.id);
            }
            if (field.equals("public_key")) {
                responseData.add("publicKey", user.publicKey);
            }
        }

        return responseData.build();
    }

    public User getUserByIdOrThrow404(String id) {
        return (User) User.findByIdOptional(id).orElseThrow(() -> new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User with given id was not found!")));
    }

    public User getUserByEmailOrThrow404(String email) {
        return (User) User.find("email", email).singleResultOptional()
                .orElseThrow(() -> new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User with given email was not found!")));
    }

}
