package club.p6e.coat.common.utils;

import lombok.experimental.Accessors;

import javax.crypto.Cipher;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Rsa Util
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class RsaUtil {

    /**
     * Key Model
     */
    @Accessors(chain = true)
    public record KeyModel(String publicKey, String privateKey) implements Serializable {
    }

    /**
     * Definition
     */
    public interface Definition {

        /**
         * Generate RSA Key Pair
         *
         * @return RSA Key Pair
         */
        KeyModel generateKeyPair();

        /**
         * Public Key Encryption
         *
         * @param publicKeyText Public Key String
         * @param text          Text
         * @return Cipher Text
         */
        String publicKeyEncryption(String publicKeyText, String text);

        /**
         * Private Key Encryption
         *
         * @param privateKeyText Private Key String
         * @param text           Text
         * @return Cipher Text
         */
        String privateKeyEncryption(String privateKeyText, String text);

        /**
         * Private Key Decryption
         *
         * @param privateKeyText Private Key String
         * @param text           Text
         * @return Plain Text
         */
        String privateKeyDecryption(String privateKeyText, String text);

    }

    /**
     * Implementation
     */
    private static class Implementation implements Definition {

        @Override
        public KeyModel generateKeyPair() {
            try {
                final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(1024);
                final KeyPair keyPair = keyPairGenerator.generateKeyPair();
                final RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
                final RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
                return new KeyModel(
                        Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded()),
                        Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded())
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String publicKeyEncryption(String publicKeyText, String text) {
            try {
                final X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyText));
                final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                final PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
                final Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String privateKeyEncryption(String privateKeyText, String text) {
            try {
                final PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyText));
                final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                final PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
                final Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, privateKey);
                return new String(cipher.doFinal(text.getBytes()), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String privateKeyDecryption(String privateKeyText, String text) {
            try {
                final PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyText));
                final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                final PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
                final Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                return new String(cipher.doFinal(Base64.getDecoder().decode(text)), StandardCharsets.UTF_8);
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
     * Generate RSA Key Pair
     *
     * @return RSA Key Pair
     */
    public static KeyModel generateKeyPair() {
        return DEFINITION.generateKeyPair();
    }

    /**
     * Public Key Encryption
     *
     * @param publicKeyText Public Key String
     * @param text          Text
     * @return Cipher Text
     */
    public static String publicKeyEncryption(String publicKeyText, String text) {
        return DEFINITION.publicKeyEncryption(publicKeyText, text);
    }

    /**
     * Private Key Encryption
     *
     * @param privateKeyText Private Key String
     * @param text           Text
     * @return Cipher Text
     */
    public static String privateKeyEncryption(String privateKeyText, String text) {
        return DEFINITION.privateKeyEncryption(privateKeyText, text);
    }

    /**
     * Private Key Decryption
     *
     * @param privateKeyText Private Key String
     * @param text           Text
     * @return Plain Text
     */
    public static String privateKeyDecryption(String privateKeyText, String text) {
        return DEFINITION.privateKeyDecryption(privateKeyText, text);
    }

}
