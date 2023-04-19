package com.nfeeds.adapter.usermanager.utils;

import com.nfeeds.adapter.usermanager.models.UserInfo;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * <p> Provides functions to generate authentication tokens and to check validity of a password. </p>
 */
public class AuthUtils {

    /**
     * <p> Generate a random sequence of character filling a list of 16 bytes using <code>SecureRandom</code> </p>
     * @return A random String.
     */
    public static String generateSalt() {
        var sr = new SecureRandom();
        var salt = new byte[16];
        sr.nextBytes(salt);
        return Arrays.toString(salt);
    }

    /**
     * <p> Given a password and a salt will generate an hash to be stored in the database for authentication </p>
     * @param psw Clear password provided.
     * @param salt Random string generated previously.
     * @return The hash of the password provided.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static String hashPassword(String psw, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var spec = new PBEKeySpec(psw.toCharArray(), salt.getBytes(), 65536, 128);
        var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        var hash = factory.generateSecret(spec).getEncoded();
        return Arrays.toString(hash);
    }

    /**
     * Checks if the password provided produce the same hash combined with the provided salt.
     * @param psw The password to validate.
     * @param salt The salt to use to create the hash.
     * @param hash The hash to check against.
     * @return True if the hash provided and the one generated are equal, False otherwise or if an error arise.
     */
    public static boolean validate(String psw, String salt, String hash) {
        try {
            return hash.equals(hashPassword(psw, salt));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return false;
        }
    }

    /**
     * Validates the password provided against the user information provided.
     * @param psw The password to validate.
     * @param info The salt and hash of the user stored in the db.
     * @return True if the hash produced using the user information and the password provided is equal to the hash stored, False otherwise or if an error arise.
     */
    public static boolean validate(String psw, UserInfo info) {
        return validate(psw,info.salt(), info.hashpsw());
    }
}
