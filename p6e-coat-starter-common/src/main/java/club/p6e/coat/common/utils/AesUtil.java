package club.p6e.coat.common.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author lidashuang
 * @version 1.0
 */
public final class AesUtil {

    public static Key generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(GeneratorUtil.random().getBytes(StandardCharsets.UTF_8));
        keyGenerator.init(256, secureRandom);
        return keyGenerator.generateKey();
    }

    public static String keyToString(Key secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static Key stringToKey(String encodedKeyString) {
        return new SecretKeySpec(Base64.getDecoder().decode(encodedKeyString), "AES");
    }

    public static byte[] encryption(byte[] plain, Key secretKey) throws Exception {
        final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plain);
    }

    public static byte[] decryption(byte[] encrypted, Key secretKey) throws Exception {
        final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encrypted);
    }

}

