package com.fisnikz.snapdrive.api.users.control;

import com.fisnikz.snapdrive.api.drive.control.DriveService;
import com.fisnikz.snapdrive.api.users.entity.CreateUserMasterPasswordRequest;
import com.fisnikz.snapdrive.api.users.entity.SignInWithGoogleResponse;
import com.fisnikz.snapdrive.api.users.entity.User;
import com.fisnikz.snapdrive.crypto.boundary.CryptoService;
import com.fisnikz.snapdrive.crypto.entity.MasterPasswordCryptoResults;
import com.fisnikz.snapdrive.crypto.entity.DerivativePasswordKeyInfo;
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
import java.lang.System.Logger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    Logger LOG;

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
        MasterPasswordCryptoResults masterPasswordCryptoResults = cryptoService.generateNewMasterPassword(plainMasterPassword);

        CreateUserMasterPasswordRequest createUserMasterPasswordRequest = new CreateUserMasterPasswordRequest();
        //dont send to server master key
        createUserMasterPasswordRequest.setPrivateKey(masterPasswordCryptoResults.getPrivateKeyBase64());
        createUserMasterPasswordRequest.setPublicKey(masterPasswordCryptoResults.getPublicKeyBase64());
        createUserMasterPasswordRequest.setDerivativeSalt(masterPasswordCryptoResults.getPbkdf2Salt());
        createUserMasterPasswordRequest.setDerivativeIterations(masterPasswordCryptoResults.getPbkdf2Iterations());
        createUserMasterPasswordRequest.setNonce(masterPasswordCryptoResults.getNonceBase64());


        return usersResourceClient.createMasterPassword(userId, createUserMasterPasswordRequest);
    }

    public Response updateMasterPassword(String newMasterPassword, String oldMasterPassword) {
        JsonObject validResults = testValidOldMasterPassword(oldMasterPassword);
        if (validResults.getInt("status") != 200) {
            return Response.status(validResults.getInt("status")).entity(validResults).build();
        }

        MasterPasswordCryptoResults results = null;
        try {
            results = cryptoService.updateUserMasterPassword(loggedInUserInfo.getUser(), oldMasterPassword, newMasterPassword);
        } catch (Exception e) {
            LOG.log(Logger.Level.ERROR, "Exception happened while trying to update user's master password!");
            throw new RuntimeException(e);
        }

        //In next line we are setting to the user the encrypted base64 private key because in that way it is stored in database
        loggedInUserInfo.getUser().setPrivateKey(results.getPrivateKeyBase64());
        loggedInUserInfo.getUser().setNonce(results.getNonceBase64());
        loggedInUserInfo.getUser().setDerivativeSalt(results.getPbkdf2Salt());
        loggedInUserInfo.getUser().setDerivativeIterations(results.getPbkdf2Iterations());

        loggedInUserInfo.unlock(newMasterPassword);

        return usersResourceClient.addOrUpdateUser(loggedInUserInfo.getUser());
    }

    private JsonObject testValidOldMasterPassword(String oldMasterPassword) {
        var status = 200;
        var message = "";

        try {
            DerivativePasswordKeyInfo derivativePasswordKeyInfo = cryptoService
                    .generateDerivativePasswordWithSalt(oldMasterPassword, loggedInUserInfo.getUser().getDerivativeSalt(), loggedInUserInfo.getUser().getDerivativeIterations());

            cryptoService.decryptPrivateKeyUsingMasterKey(loggedInUserInfo.getUser().getPrivateKey(),
                    derivativePasswordKeyInfo.getSecretKey(), loggedInUserInfo.getUser().getNonce());
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
