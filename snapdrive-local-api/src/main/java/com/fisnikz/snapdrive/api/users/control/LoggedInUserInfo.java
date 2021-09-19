package com.fisnikz.snapdrive.api.users.control;

import com.fisnikz.snapdrive.api.users.entity.SignInWithGoogleResponse;
import com.fisnikz.snapdrive.api.users.entity.User;
import com.fisnikz.snapdrive.crypto.boundary.CryptoService;
import com.fisnikz.snapdrive.crypto.entity.DerivativePasswordKeyInfo;
import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
//@Startup
public class LoggedInUserInfo {

    @Inject
    @RestClient
    UsersResourceClient usersResourceClient;

//    @PostConstruct
//    public void init() {
//
//        Response response = usersResourceClient.signInWithGoogle(new SignInWithGoogleResponse("jessbrooks131@gmail.com", "104023429820123018703"));
//        User user = response.readEntity(User.class);
//        setUser(user);
//        unlock("1234");
//
//        System.out.println("INIT+++++++++++++++++++++++++++++");
//        System.out.println(user.getPrivateKey());
//        System.out.println("INIT++++++++++++++++++++++++++++++++");
//    }


    @Inject
    CryptoService cryptoService;

    private User user;
    private PrivateKey userPrivateKey;

    public boolean unlock(String masterPassword) {
        if (this.isLoggedIn()) {
            try {
                decryptedPrivateKey(masterPassword, user.getDerivativeSalt(), user.getNonce(), user.getPrivateKey());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        System.out.println("NO USER LOGGED IN");
        return false;
    }

    private void decryptedPrivateKey(String masterPassword, String masterPasswordSaltBase64, String nonceBase64, String encryptedPrivateKeyBase64) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        DerivativePasswordKeyInfo derivativePasswordKeyInfo = cryptoService.generateDerivativePasswordWithSalt(masterPassword, masterPasswordSaltBase64, 65536);
        this.userPrivateKey = cryptoService.decryptPrivateKeyUsingMasterKey(encryptedPrivateKeyBase64, derivativePasswordKeyInfo.getSecretKey(), nonceBase64);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PrivateKey getUserPrivateKey() {
        return userPrivateKey;
    }

    public void setUserDecryptedPrivateKey(PrivateKey userPrivateKey) {
        this.userPrivateKey = userPrivateKey;
    }

    public boolean isLoggedIn() {
        return this.user != null;
    }

    public void logout() {
        this.user = null;
        this.userPrivateKey = null;
    }
}
