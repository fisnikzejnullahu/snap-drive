package com.fisnikz.snapdrive.crypto.boundary;

import com.fisnikz.snapdrive.api.users.entity.User;
import com.fisnikz.snapdrive.crypto.control.AesService;
import com.fisnikz.snapdrive.crypto.control.ChaChaService;
import com.fisnikz.snapdrive.crypto.control.PBKDF2Service;
import com.fisnikz.snapdrive.crypto.control.RsaService;
import com.fisnikz.snapdrive.crypto.entity.DerivativePasswordKeyInfo;
import com.fisnikz.snapdrive.crypto.entity.FileEncryptionFinalResult;
import com.fisnikz.snapdrive.crypto.entity.FileEncryptionInfo;
import com.fisnikz.snapdrive.crypto.entity.MasterPasswordCryptoResults;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.JsonbBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
public class CryptoService {

    private final AesService aesService;
    private final PBKDF2Service pbkdf2Service;
    private final RsaService rsaService;
    private final ChaChaService chaChaService;

    public CryptoService() {
        this.aesService = new AesService();
        this.pbkdf2Service = new PBKDF2Service();
        this.rsaService = new RsaService();
        this.chaChaService = new ChaChaService();
    }

    public CryptoService(AesService aesService, PBKDF2Service pbkdf2Service, RsaService rsaService, ChaChaService chaChaService) {
        this.aesService = aesService;
        this.pbkdf2Service = pbkdf2Service;
        this.rsaService = rsaService;
        this.chaChaService = chaChaService;
    }

    public static String encodeToBase64(byte[] array) {
        return Base64.getUrlEncoder().encodeToString(array);
    }

    public static byte[] decodeFromBase64(String encoded) {
        return Base64.getUrlDecoder().decode(encoded);
    }

    public RsaService getRsaService() {
        return rsaService;
    }

    public byte[] encryptPrivateKeyWithUserKey(PrivateKey privateKey, SecretKey userKey, byte[] nonce) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        return this.chaChaService.encrypt(privateKey.getEncoded(), userKey, nonce, 1);
    }

    public byte[] encryptFileKeyWithPubKey(SecretKey secretKey, PublicKey publicKey) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        return this.rsaService.encrypt(secretKey.getEncoded(), publicKey);
    }

    public SecretKey decryptFileKeyWithPrivateKey(byte[] bytes, PrivateKey privateKey) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] decrypted = this.rsaService.decrypt(bytes, privateKey);
        return new SecretKeySpec(decrypted, "AES");
    }

    public FileEncryptionInfo encryptFile(File inputFile) throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        return this.aesService.encryptFile(
                Files.readAllBytes(Path.of(inputFile.getAbsolutePath())),
                inputFile.getName()
        );
    }

    public void encrypt(File inputFile, File outputFolder, User user) throws NoSuchPaddingException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException,
            IOException {

        File configFile = new File(outputFolder.getAbsolutePath() + "/.config");
        configFile.createNewFile();

        FileEncryptionInfo fileEncryptionResult = this.encryptFile(inputFile);

        byte[] encryptedFileKey = this.encryptFileKeyWithPubKey(fileEncryptionResult.getSecretKey(),
                (PublicKey) this.generateRSAKeyFromBase64(user.getPublicKey(), false));

        String base64InitializationVector = encodeToBase64(fileEncryptionResult.getInitializationVectorSpec().getIV());
        String base64EncFileKey = encodeToBase64(encryptedFileKey);

        FileEncryptionFinalResult result = new FileEncryptionFinalResult(user.getId(), user.getPublicKey(), base64EncFileKey, base64InitializationVector);
        Files.writeString(Path.of(configFile.getAbsolutePath()), JsonbBuilder.create().toJson(result), StandardOpenOption.WRITE);

        File encryptedFile = new File(outputFolder.getAbsolutePath() + "/" + fileEncryptionResult.getEncryptedFileName());
        encryptedFile.createNewFile();

        Files.write(Path.of(encryptedFile.getAbsolutePath()), fileEncryptionResult.getEncryptedInput(), StandardOpenOption.WRITE);
    }

    public byte[] decryptFile(File inputFile, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException {
        return aesService.decrypt(readFileBytes(inputFile), key, iv);
    }

    public byte[] decryptBytes(byte[] bytes, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException {
        return aesService.decrypt(bytes, key, iv);
    }

    private byte[] readFileBytes(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        byte[] inputBytes = new byte[(int) file.length()];
        inputStream.read(inputBytes);
        inputStream.close();

        return inputBytes;
    }

    public DerivativePasswordKeyInfo generateDerivativePassword(String password) {
        return pbkdf2Service.generateKey(password);
    }

    public PrivateKey decryptPrivateKeyUsingMasterKey(String privateKeyBase64, SecretKey secretKey, String nonceBase64) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        byte[] decryptedBytes = chaChaService.decrypt(decodeFromBase64(privateKeyBase64), secretKey, decodeFromBase64(nonceBase64), 1);
        return (PrivateKey) rsaService.generatePrivateKeyFromBytesArray(decryptedBytes, true);
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

    public KeyPair generateRSAKeyPair() {
        return this.rsaService.generateKeyPair();
    }

    public Key generateRSAKeyFromBase64(String key, boolean isPrivateKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] bytes = decodeFromBase64(key);
        return this.rsaService.generatePrivateKeyFromBytesArray(bytes, isPrivateKey);
    }

    public DerivativePasswordKeyInfo generateDerivativePasswordWithSalt(String password, String derivativeSaltBase64, int derivativeIterations) {
        byte[] salt = decodeFromBase64(derivativeSaltBase64);
        return this.pbkdf2Service.generateKeyWithGivenSalt(password, salt);
    }

    public MasterPasswordCryptoResults generateNewMasterPassword(String masterPassword) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        KeyPair rsaKeyPair = this.generateRSAKeyPair();
        DerivativePasswordKeyInfo passwordKeyInfo = this.generateDerivativePassword(masterPassword);

        byte[] nonce = this.chaChaService.generateNonce();
        String base64Nonce = CryptoService.encodeToBase64(nonce);

        byte[] encryptedPrivateKey = this.encryptPrivateKeyWithUserKey(rsaKeyPair.getPrivate(), passwordKeyInfo.getSecretKey(), nonce);
        return new MasterPasswordCryptoResults(encodeToBase64(encryptedPrivateKey), encodeToBase64(rsaKeyPair.getPublic().getEncoded()),
                base64Nonce, encodeToBase64(passwordKeyInfo.getSalt()), passwordKeyInfo.getIterations());
    }

    public MasterPasswordCryptoResults updateUserMasterPassword(User user, String oldMasterPassword, String newMasterPassword) throws InvalidKeySpecException, NoSuchAlgorithmException,
            IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException,
            IOException, InvalidAlgorithmParameterException {

        DerivativePasswordKeyInfo oldDerivativePasswordKeyInfo = generateDerivativePasswordWithSalt(oldMasterPassword, user.getDerivativeSalt(), user.getDerivativeIterations());
        PrivateKey privateKey = this.decryptPrivateKeyUsingMasterKey(user.getPrivateKey(), oldDerivativePasswordKeyInfo.getSecretKey(), user.getNonce());

        DerivativePasswordKeyInfo newPasswordKeyInfo = this.generateDerivativePassword(newMasterPassword);

        byte[] nonce = this.chaChaService.generateNonce();
        String base64Nonce = CryptoService.encodeToBase64(nonce);
        byte[] encryptedPrivateKey = this.encryptPrivateKeyWithUserKey(privateKey, newPasswordKeyInfo.getSecretKey(), nonce);

        return new MasterPasswordCryptoResults(encodeToBase64(encryptedPrivateKey), user.getPublicKey(),
                base64Nonce, encodeToBase64(newPasswordKeyInfo.getSalt()), newPasswordKeyInfo.getIterations());
    }

}
