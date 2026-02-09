package club.p6e.coat.common.utils;

import lombok.Data;
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
public final class RsaUtil {

    /**
     * 密钥对象
     */
    @Data
    @Accessors(chain = true)
    public static class KeyModel implements Serializable {
        private final String publicKey;
        private final String privateKey;

        public KeyModel(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }

    /**
     * 定义类
     */
    public interface Definition {

        /**
         * 构建 RSA 密钥对
         *
         * @return RSA 密钥对
         */
        public KeyModel generateKeyPair() throws Exception;

        /**
         * 公钥加密
         *
         * @param publicKeyText 公钥字符内容
         * @param text          待加密内容
         * @return 密文
         */
        public String publicKeyEncryption(String publicKeyText, String text) throws Exception;

        /**
         * 私钥加密
         *
         * @param privateKeyText 私钥字符内容
         * @param text           待加密内容
         * @return 密文
         */
        public String privateKeyEncryption(String privateKeyText, String text) throws Exception;

        /**
         * 私钥解密
         *
         * @param privateKeyText 私钥字符内容
         * @param text           待解密内容
         * @return 明文
         */
        public String privateKeyDecryption(String privateKeyText, String text) throws Exception;

    }

    /**
     * 实现类
     */
    private static class Implementation implements Definition {

        @Override
        public KeyModel generateKeyPair() throws Exception {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            final KeyPair keyPair = keyPairGenerator.generateKeyPair();
            final RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
            final RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
            return new KeyModel(
                    Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded()),
                    Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded())
            );
        }

        @Override
        public String publicKeyEncryption(String publicKeyText, String text) throws Exception {
            final X509EncodedKeySpec x509EncodedKeySpec =
                    new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyText));
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8)));
        }

        @Override
        public String privateKeyEncryption(String privateKeyText, String text) throws Exception {
            final PKCS8EncodedKeySpec pkcs8EncodedKeySpec =
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyText));
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return new String(cipher.doFinal(text.getBytes()), StandardCharsets.UTF_8);
        }

        @Override
        public String privateKeyDecryption(String privateKeyText, String text) throws Exception {
            final PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 =
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyText));
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(text)), StandardCharsets.UTF_8);
        }

    }

    /**
     * 默认的 RSA 实现的对象
     */
    private static Definition DEFINITION = new Implementation();

    /**
     * 设置 RSA 实现的对象
     *
     * @param implementation RSA 实现的对象
     */
    public static void set(Definition implementation) {
        DEFINITION = implementation;
    }

    /**
     * 构建 RSA 密钥对
     *
     * @return RSA 密钥对
     */
    public static KeyModel generateKeyPair() throws Exception {
        return DEFINITION.generateKeyPair();
    }

    /**
     * 公钥加密
     *
     * @param publicKeyText 公钥字符内容
     * @param text          待加密内容
     * @return 密文
     */
    public static String publicKeyEncryption(String publicKeyText, String text) throws Exception {
        return DEFINITION.publicKeyEncryption(publicKeyText, text);
    }

    /**
     * 私钥加密
     *
     * @param privateKeyText 私钥字符内容
     * @param text           待加密内容
     * @return 密文
     */
    public static String privateKeyEncryption(String privateKeyText, String text) throws Exception {
        return DEFINITION.privateKeyEncryption(privateKeyText, text);
    }

    /**
     * 私钥解密
     *
     * @param privateKeyText 私钥字符内容
     * @param text           待解密内容
     * @return 明文
     */
    public static String privateKeyDecryption(String privateKeyText, String text) throws Exception {
        return DEFINITION.privateKeyDecryption(privateKeyText, text);
    }

}
