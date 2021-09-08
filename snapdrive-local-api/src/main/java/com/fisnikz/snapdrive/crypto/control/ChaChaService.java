package com.fisnikz.snapdrive.crypto.control;

import javax.crypto.*;
import javax.crypto.spec.ChaCha20ParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author Fisnik Zejnullahu
 */
public class ChaChaService {

    private static final String ALGORITHM = "ChaCha20";
    private final static int KEY_BITS_LENGTH = 256;
    private final static int NONCE_BITS_LENGTH = 96;

    public byte[] encrypt(byte[] pText, SecretKey key, byte[] nonce, int counter) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        ChaCha20ParameterSpec param = new ChaCha20ParameterSpec(nonce, counter);
        cipher.init(Cipher.ENCRYPT_MODE, key, param);

        return cipher.doFinal(pText);
    }

    public byte[] decrypt(byte[] cText, SecretKey key, byte[] nonce, int counter) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        ChaCha20ParameterSpec param = new ChaCha20ParameterSpec(nonce, counter);
        cipher.init(Cipher.DECRYPT_MODE, key, param);

        return cipher.doFinal(cText);
    }

    public SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_BITS_LENGTH, SecureRandom.getInstanceStrong());
        return keyGen.generateKey();
    }

    public byte[] generateNonce() {
        byte[] newNonce = new byte[NONCE_BITS_LENGTH / 8];
        new SecureRandom().nextBytes(newNonce);
        return newNonce;
    }
}
