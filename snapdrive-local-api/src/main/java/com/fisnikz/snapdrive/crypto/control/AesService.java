package com.fisnikz.snapdrive.crypto.control;

import com.fisnikz.snapdrive.crypto.entity.*;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.fisnikz.snapdrive.crypto.boundary.CryptoService.encodeToBase64;

/**
 * @author Fisnik Zejnullahu
 */
public class AesService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private final static int KEY_BITS_LENGTH = 256;
    private final static int TAG_BITS_LENGTH = 128;
    private final static int IV_BITS_LENGTH = 96;

    public FileEncryptionInfo encryptFile(byte[] input, String fileName) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        SecretKey secretKey = generateSecretKey();
        IvParameterSpec iv = generateIv();

        byte[] contentCipherText = doEncrypt(input, secretKey, iv);
        byte[] fileNameCipherText = doEncrypt(fileName.getBytes(), secretKey, iv);

        return new FileEncryptionInfo(secretKey, iv, contentCipherText, encodeToBase64(fileNameCipherText));
    }

    public BasicFileEncryptionInfo encryptWithKey(byte[] input, SecretKey secretKey) throws NoSuchPaddingException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        IvParameterSpec iv = generateIv();
        byte[] cipherText = doEncrypt(input, secretKey, iv);

        return new FileKeyEncryptionInfo(secretKey, iv, cipherText);
    }

    private byte[] doEncrypt(byte[] input, SecretKey secretKey, IvParameterSpec iv) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(ALGORITHM);

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_BITS_LENGTH, iv.getIV());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
        return cipher.doFinal(input);
    }

    public byte[] decrypt(byte[] cipherText, SecretKey key,
                          IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_BITS_LENGTH, iv.getIV());

        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);
        return cipher.doFinal(cipherText);
    }

    private SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(KEY_BITS_LENGTH);

            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private IvParameterSpec generateIv() {
        byte[] iv = new byte[IV_BITS_LENGTH / 8];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}
