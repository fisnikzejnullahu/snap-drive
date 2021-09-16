package com.fisnikz.snapdrive.api.users.control;

import com.fisnikz.snapdrive.api.drive.control.DriveService;
import com.fisnikz.snapdrive.api.users.entity.CreateUserMasterPasswordRequest;
import com.fisnikz.snapdrive.api.users.entity.LoggedInUserInfo;
import com.fisnikz.snapdrive.api.users.entity.SignInWithGoogleResponse;
import com.fisnikz.snapdrive.api.users.entity.User;
import com.fisnikz.snapdrive.crypto.boundary.CryptoService;
import com.fisnikz.snapdrive.crypto.entity.MasterPasswordCryptoResults;
import com.fisnikz.snapdrive.crypto.entity.MasterPasswordKeyInfo;
import com.fisnikz.snapdrive.logging.Logged;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
@Logged
public class UsersService {

    @Inject
    @RestClient
    UsersResourceClient usersResourceClient;

    @Inject
    CryptoService cryptoService;

    @Inject
    LoggedInUserInfo loggedInUserInfo;

    @Inject
    DriveService driveService;

    @Inject
    GoogleAuthService googleAuthService;

    public Response signInWithGoogle(String authorizationCode) {
        SignInWithGoogleResponse signInWithGoogleResponse = googleAuthService.signInWithGoogle(authorizationCode);
        Response response = usersResourceClient.signInWithGoogle(signInWithGoogleResponse);
        User user = response.readEntity(User.class);
        System.out.println(JsonbBuilder.create().toJson(user));
        loggedInUserInfo.setUser(user);

        return Response.status(response.getStatus()).entity(user).build();
    }

    public User createMasterPassword(String userId, String plainMasterPassword) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, WebApplicationException {
        MasterPasswordCryptoResults masterPasswordCryptoResults = cryptoService.doCryptoToMasterPassword(plainMasterPassword);

        CreateUserMasterPasswordRequest createUserMasterPasswordRequest = new CreateUserMasterPasswordRequest();
        //dont send to server master key
        createUserMasterPasswordRequest.setPrivateKey(masterPasswordCryptoResults.getPrivateKeyBase64());
        createUserMasterPasswordRequest.setPublicKey(masterPasswordCryptoResults.getPublicKeyBase64());
        createUserMasterPasswordRequest.setDerivativeSalt(masterPasswordCryptoResults.getPbkdf2Salt());
        createUserMasterPasswordRequest.setDerivativeIterations(masterPasswordCryptoResults.getPbkdf2Iterations());
        createUserMasterPasswordRequest.setNonce(masterPasswordCryptoResults.getNonceBase64());


        return usersResourceClient.createMasterPassword(userId, createUserMasterPasswordRequest);
    }

    public Response updateProfile(String newUsername) {
        loggedInUserInfo.getUser().setEmail(newUsername);
        return usersResourceClient.updateUser(loggedInUserInfo.getUser().getId(), loggedInUserInfo.getUser());
    }

    public Response updateMasterPassword(String newMasterPassword, String oldMasterPassword) {
        JsonObject validResults = testValidOldMasterPassword(oldMasterPassword);
        if (validResults.getInt("status") != 200) {
            return Response.status(validResults.getInt("status")).entity(validResults).build();
        }

        MasterPasswordCryptoResults results = driveService.updateMasterPassword(newMasterPassword, oldMasterPassword);

        /*
            In above line where we call driveService.updateMasterPw(), results contain private key which is encrypted with masterKey
            So in order to reset loggedInUser's privateKey, we first need to decrypt the new private key and then set it to loggedInUser
         */
        MasterPasswordKeyInfo masterPasswordKeyInfo = cryptoService
                .generateDerivativePasswordWithSalt(newMasterPassword, results.getPbkdf2Salt(), results.getPbkdf2Iterations());
        PrivateKey privateKey;
        try {
            privateKey = cryptoService.decryptPrivateKeyUsingMasterKey(results.getPrivateKeyBase64(), masterPasswordKeyInfo.getSecretKey(), results.getNonceBase64());
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof SecurityException) {
                JsonObject data = Json.createObjectBuilder().add("message", "We couldn't decrypt files with given old master password! Please try again!")
                        .build();


                return Response.status(403).entity(data).build();
            } else {
                JsonObject data = Json.createObjectBuilder().add("message", "Something went wrong!")
                        .build();
                return Response.status(500).entity(data).build();
            }
        }

        loggedInUserInfo.setUserDecryptedPrivateKey(privateKey);

        //In next line we are setting to the user the encrypted base64 private key because in that way it is stored in database
        loggedInUserInfo.getUser().setPrivateKey(results.getPrivateKeyBase64());
        loggedInUserInfo.getUser().setPublicKey(results.getPublicKeyBase64());
        loggedInUserInfo.getUser().setNonce(results.getNonceBase64());
        loggedInUserInfo.getUser().setDerivativeSalt(results.getPbkdf2Salt());
        loggedInUserInfo.getUser().setDerivativeIterations(results.getPbkdf2Iterations());

        return usersResourceClient.updateUser(loggedInUserInfo.getUser().getId(), loggedInUserInfo.getUser());
    }

    private JsonObject testValidOldMasterPassword(String oldMasterPassword) {
        var status = 200;
        var message = "";

        try {
            MasterPasswordKeyInfo masterPasswordKeyInfo = cryptoService
                    .generateDerivativePasswordWithSalt(oldMasterPassword, loggedInUserInfo.getUser().getDerivativeSalt(), loggedInUserInfo.getUser().getDerivativeIterations());

            cryptoService.decryptPrivateKeyUsingMasterKey(loggedInUserInfo.getUser().getPrivateKey(),
                    masterPasswordKeyInfo.getSecretKey(), loggedInUserInfo.getUser().getNonce());
        } catch (Exception e) {
            if (e instanceof InvalidKeySpecException) {
                message = "We couldn't decrypt files with given old master password! Please try again!";
                status = 403;
            } else {
                message = "Something went wrong!";
                status = 500;
            }
        }
        return Json.createObjectBuilder()
                .add("message", message)
                .add("status", status)
                .build();
    }

    public void logout() {
        loggedInUserInfo.logout();
    }
}
