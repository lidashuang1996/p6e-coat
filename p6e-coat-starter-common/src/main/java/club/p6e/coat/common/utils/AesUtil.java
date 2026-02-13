package club.p6e.coat.common.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Aes Util
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class AesUtil {

    /**
     * Definition
     */
    public interface Definition {

        /**
         * Generate Key
         *
         * @return Aes Key Object
         */
        Key generateKey() throws NoSuchAlgorithmException;

        /**
         * Execute Key To String
         *
         * @param key Aes Key Object
         * @return Aes Key String
         */
        String executeKeyToString(Key key);

        /**
         * Execute String To Key
         *
         * @param string Aes Key String
         * @return Aes Key Object
         */
        Key executeStringToKey(String string);

        /**
         * Encryption
         *
         * @param bytes Byte Array Object
         * @param key   Aes Key Object
         * @return Aes Encryption Byte Array Object
         */
        byte[] encryption(byte[] bytes, Key key);

        /**
         * Decryption
         *
         * @param bytes Byte Array Object
         * @param key   Aes Key Object
         * @return Aes Decryption Byte Array Object
         */
        byte[] decryption(byte[] bytes, Key key);

    }

    /**
     * Implementation
     */
    private static class Implementation implements Definition {

        @Override
        public Key generateKey() throws NoSuchAlgorithmException {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(GeneratorUtil.random().getBytes(StandardCharsets.UTF_8));
            keyGenerator.init(256, secureRandom);
            return keyGenerator.generateKey();
        }

        @Override
        public String executeKeyToString(Key key) {
            return Base64.getEncoder().encodeToString(key.getEncoded());
        }

        @Override
        public Key executeStringToKey(String string) {
            return new SecretKeySpec(Base64.getDecoder().decode(string), "AES");
        }

        @Override
        public byte[] encryption(byte[] bytes, Key key) {
            try {
                final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return cipher.doFinal(bytes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public byte[] decryption(byte[] bytes, Key key) {
            try {
                final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key);
                return cipher.doFinal(bytes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Default Definition Implementation Object
     */
    private static Definition DEFINITION = new Implementation();

    /**
     * Set Definition Implementation Object
     *
     * @param implementation Definition Implementation Object
     */
    public static void set(Definition implementation) {
        DEFINITION = implementation;
    }

    /**
     * Generate Key
     *
     * @return Aes Key Object
     */
    public static Key generateKey() throws NoSuchAlgorithmException {
        return DEFINITION.generateKey();
    }

    /**
     * Execute Key To String
     *
     * @param key Aes Key Object
     * @return Aes Key String
     */
    public static String executeKeyToString(Key key) {
        return DEFINITION.executeKeyToString(key);
    }

    /**
     * Execute String To Key
     *
     * @param string Aes Key String
     * @return Aes Key Object
     */
    public static Key executeStringToKey(String string) {
        return DEFINITION.executeStringToKey(string);
    }

    /**
     * Encryption
     *
     * @param bytes Byte Array Object
     * @param key   Aes Key Object
     * @return Aes Encryption Byte Array Object
     */
    public static byte[] encryption(byte[] bytes, Key key) {
        return DEFINITION.encryption(bytes, key);
    }

    /**
     * Decryption
     *
     * @param bytes Byte Array Object
     * @param key   Aes Key Object
     * @return Aes Decryption Byte Array Object
     */
    public static byte[] decryption(byte[] bytes, Key key) {
        return DEFINITION.decryption(bytes, key);
    }

}

