
package com.hanlin.fadp.base.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.DESedeKeySpec;

import com.hanlin.fadp.base.util.GeneralException;

/**
 * Utility class for doing DESded (3DES) Two-Way Encryption
 *
 */
public class DesCrypt {

    public static final String module = DesCrypt.class.getName();

    public static Key generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("DESede");

        // generate the DES3 key
        return keyGen.generateKey();
    }

    public static byte[] encrypt(Key key, byte[] bytes) throws GeneralException {
        Cipher cipher = DesCrypt.getCipher(key, Cipher.ENCRYPT_MODE);
        byte[] encBytes = null;
        try {
            encBytes = cipher.doFinal(bytes);
        } catch (IllegalStateException e) {
            throw new GeneralException(e);
        } catch (IllegalBlockSizeException e) {
            throw new GeneralException(e);
        } catch (BadPaddingException e) {
            throw new GeneralException(e);
        }
        return encBytes;
    }

    public static byte[] decrypt(Key key, byte[] bytes) throws GeneralException {
        Cipher cipher = DesCrypt.getCipher(key, Cipher.DECRYPT_MODE);
        byte[] decBytes = null;
        try {
            decBytes = cipher.doFinal(bytes);
        } catch (IllegalStateException e) {
            throw new GeneralException(e);
        } catch (IllegalBlockSizeException e) {
            throw new GeneralException(e);
        } catch (BadPaddingException e) {
            throw new GeneralException(e);
        }
        return decBytes;
    }

    public static Key getDesKey(byte[] rawKey) throws GeneralException {
        SecretKeyFactory skf = null;
        try {
            skf = SecretKeyFactory.getInstance("DESede");
        } catch (NoSuchAlgorithmException e) {
            throw new GeneralException(e);
        }

        // load the raw key
        if (rawKey.length > 0) {
            DESedeKeySpec desedeSpec1 = null;
            try {
                desedeSpec1 = new DESedeKeySpec(rawKey);
            } catch (InvalidKeyException e) {
                throw new GeneralException(e);
            }

            // create the SecretKey Object
            Key key = null;
            try {
                key = skf.generateSecret(desedeSpec1);
            } catch (InvalidKeySpecException e) {
                throw new GeneralException(e);
            }
            return key;
        } else {
            throw new GeneralException("Not a valid DESede key!");
        }
    }

    // return a cipher for a key - DESede/CBC/PKCS5Padding IV = 0
    protected static Cipher getCipher(Key key, int mode) throws GeneralException {
        byte[] zeros = { 0, 0, 0, 0, 0, 0, 0, 0 };
        IvParameterSpec iv = new IvParameterSpec(zeros);

        // create the Cipher - DESede/CBC/NoPadding
        Cipher encCipher = null;
        try {
            encCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            throw new GeneralException(e);
        } catch (NoSuchPaddingException e) {
            throw new GeneralException(e);
        }
        try {
            encCipher.init(mode, key, iv);
        } catch (InvalidKeyException e) {
            throw new GeneralException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new GeneralException(e);
        }
        return encCipher;
    }
}
