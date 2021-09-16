package com.fisnikz.snapdrive.api.users.entity;

import com.fisnikz.snapdrive.api.users.control.UsersResourceClient;
import com.fisnikz.snapdrive.crypto.boundary.CryptoService;
import com.fisnikz.snapdrive.crypto.entity.MasterPasswordKeyInfo;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
public class LoggedInUserInfo {

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
