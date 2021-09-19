package com.fisnikz.snapdrive.crypto.control;

import com.fisnikz.snapdrive.crypto.entity.DerivativePasswordKeyInfo;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * @author Fisnik Zejnullahu
 */
public class PBKDF2Service {

    private static final int SALT_BYTES = 128;
    private static final int HASH_BYTES = 256;
    private static final int PBKDF2_ITERATIONS = 65536;

    public DerivativePasswordKeyInfo generateKey(String password) {
        try {
            byte[] salt = generateSalt();
            SecretKey secretKey = generate(password.toCharArray(), salt);
            return new DerivativePasswordKeyInfo(secretKey, salt, PBKDF2_ITERATIONS);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public DerivativePasswordKeyInfo generateKeyWithGivenSalt(String password, byte[] salt) {
        try {
            SecretKey secretKey = generate(password.toCharArray(), salt);
            return new DerivativePasswordKeyInfo(secretKey, salt, PBKDF2_ITERATIONS);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        return salt;
    }

    private SecretKey generate(char[] encryptedKeyChars, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(encryptedKeyChars, salt, PBKDF2_ITERATIONS, HASH_BYTES);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//        return factory.generateSecret(spec);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "ChaCha20");
    }
}
