package com.fisnikz.snapdrive.users.control;

import com.fisnikz.snapdrive.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.logging.Logged;
import com.fisnikz.snapdrive.users.entity.CreateUserMasterPasswordRequest;
import com.fisnikz.snapdrive.users.entity.User;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

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
import java.lang.System.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
@Transactional
@Logged
public class UsersService {

    @Inject
    Jsonb jsonb;

    @Inject
    FirebaseService firebaseService;

    @Inject
    Logger LOG;

    public Response signInWithGoogle(String email, String googleId) {
        try {
            // new email request
            User userByEmail = getUserByEmail(email);

            if (userByEmail == null) {
                userByEmail = new User(googleId, email);
                firebaseService.addOrUpdateUser(userByEmail);
                return Response.status(201).entity(userByEmail).build();
            }

            return Response.status(200).entity(userByEmail).build();

        } catch (ExecutionException | InterruptedException e) {
            LOG.log(Logger.Level.ERROR, "Exception happened while trying to signIn with google! Error with http status (500) was thrown to client!");
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInternalError());
        }

//        JsonObject responseData = Json.createObjectBuilder()
//                .add("id", email.getId())
//                .add("email", email.getEmail())
//                .add("googleId", email.getGoogleId())
//                .add("registerAt", email.getRegisterAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")))
//                .build();

    }

    public Response addOrUpdate(User user) {
        var responseStatusCode = 200;
        try {
            // new user request
            User userByEmail = getUserByEmail(user.getEmail());

            if (userByEmail == null) {
                user.setId(UUID.randomUUID().toString());
                user.setRegisterAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")));

                responseStatusCode = 201;
            } else {
                System.out.println("UPDATEING....");
                System.out.println(jsonb.toJson(user));
                user.setId(userByEmail.getId());
            }

            firebaseService.addOrUpdateUser(user);
        } catch (ExecutionException | InterruptedException e) {
            LOG.log(Logger.Level.ERROR, "Exception happened while trying to signIn with google! Error with http status (500) was thrown to client!");
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInternalError());
        }
        System.out.println("responseStatusCode = " + responseStatusCode);
        return Response.status(responseStatusCode).entity(user).build();
    }

    public User createMasterPassword(String userId, CreateUserMasterPasswordRequest createUserMasterPasswordRequest) {
        User user = getUserByIdOrThrow404(userId);

        user.setPrivateKey(createUserMasterPasswordRequest.getPrivateKey());
        user.setPublicKey(createUserMasterPasswordRequest.getPublicKey());
        user.setDerivativeSalt(createUserMasterPasswordRequest.getDerivativeSalt());
        user.setDerivativeIterations(createUserMasterPasswordRequest.getDerivativeIterations());
        user.setNonce(createUserMasterPasswordRequest.getNonce());

        try {
            firebaseService.addOrUpdateUser(user);
        } catch (ExecutionException | InterruptedException e) {
            LOG.log(Logger.Level.ERROR, "Exception happened while trying to create user's master password! Error with http status (500) was thrown to client!");
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInternalError());
        }

        return user;
    }

    public JsonObject getUserWithGivenFields(String email, String requestedFields) {
        User user = getUserByEmail(email);

        if (user == null) {
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User with given email was not found!"));
        }

        if (requestedFields.equals("*")) {
            return Json.createReader(new StringReader(jsonb.toJson(user))).readObject();
        }

        JsonObjectBuilder responseData = Json.createObjectBuilder()
                .add("email", user.getEmail());

        for (String field : requestedFields.split(",")) {
            if (field.equals("userId")) {
                responseData.add("userId", user.getId());
            }
            if (field.equals("public_key")) {
                responseData.add("publicKey", user.getPublicKey());
            }
        }

        return responseData.build();
    }

    public User getUserByEmail(String email) {
        try {
            QuerySnapshot snapshot = firebaseService.findUserByEmail(email).get();
            if (snapshot.isEmpty()) {
                return null;
            }
            return snapshot.getDocuments().get(0).toObject(User.class);
        } catch (InterruptedException | ExecutionException e) {
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User with given email was not found!"));
        }
    }

    public User getUserByIdOrThrow404(String userId) {
        try {
            DocumentSnapshot snapshot = firebaseService.findUserById(userId).get();
            if (!snapshot.exists()) {
                throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User with given username was not found!"));
            }
            return snapshot.toObject(User.class);
        } catch (InterruptedException | ExecutionException e) {
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(404, "User with given username was not found!"));
        }
    }

}
