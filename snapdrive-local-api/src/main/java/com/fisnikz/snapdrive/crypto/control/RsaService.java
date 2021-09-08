package com.fisnikz.snapdrive.crypto.control;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Fisnik Zejnullahu
 */
public class RsaService {

    private static final String ALGORITHM = "RSA";
    private final static int KEY_BITS_LENGTH = 2048;

    public byte[] encrypt(byte[] input, Key key) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeyException {

        Cipher cipher = Cipher.getInstance(ALGORITHM);

        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    public byte[] decrypt(byte[] cipherText, Key key) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherText);
    }

    public Key generatePrivateKeyFromBytesArray(byte[] arr, boolean isPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return isPrivateKey ? factory.generatePrivate(new PKCS8EncodedKeySpec(arr)) : factory.generatePublic(new X509EncodedKeySpec(arr));
    }

    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(KEY_BITS_LENGTH);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
