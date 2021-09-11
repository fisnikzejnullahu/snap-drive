package com.fisnikz.snapdrive.api.users.entity;

import com.fisnikz.snapdrive.api.users.control.UsersResourceClient;
import com.fisnikz.snapdrive.crypto.boundary.CryptoService;
import com.fisnikz.snapdrive.crypto.entity.MasterPasswordKeyInfo;
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

    @Inject
    CryptoService cryptoService;

    private User user;
    private PrivateKey userPrivateKey;

    @PostConstruct
    public void init() {
//        this.login("cniku2", "123");
//        this.unlockFiles("123");
    }

    //OLD
//    public User login(String username, String password) {
//        System.out.println("LOGGING IN..............");
//        Response response = usersResourceClient.login(new UserLoginRequest(username, password));
//        if (response.getStatus() == 200) {
//            String userJson = response.readEntity(String.class);
//            this.user = JsonbBuilder.create().fromJson(userJson, User.class);
//            return this.user;
//        }
//        return null;
//    }

    public User login(String username, String password) {
        System.out.println("LOGGING IN..............");
        Response response = usersResourceClient.login(new UserLoginRequest(username, password));
        if (response.getStatus() == 200) {
            System.out.println("LOGGED IN");
            this.user = response.readEntity(User.class);
            return this.user;
        }
        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
        return null;
    }

    public boolean unlockFiles(String masterPassword) {
        if (this.isLoggedIn()) {
            System.out.println(this.user.getId());
            System.out.println(this.user.getPrivateKey());
            try {
                decryptedPrivateKey(masterPassword, user.getDerivativeSalt(), user.getNonce(), user.getPrivateKey());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        System.out.println(this.getUser());
        System.out.println("JO LOGIN");
        return false;
    }

    private void decryptedPrivateKey(String masterPassword, String masterPasswordSaltBase64, String nonceBase64, String encryptedPrivateKeyBase64) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        MasterPasswordKeyInfo masterPasswordKeyInfo = cryptoService.generateDerivativePasswordWithSalt(masterPassword, masterPasswordSaltBase64, 65536);
        this.userPrivateKey = cryptoService.decryptPrivateKeyUsingMasterKey(encryptedPrivateKeyBase64, masterPasswordKeyInfo.getSecretKey(), nonceBase64);
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
