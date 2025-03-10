package club.p6e.coat.common.utils;

import java.security.MessageDigest;

/**
 * @author lidashuang
 * @version 1.0
 */
public final class Md5Util {

    public static String execute(String content) {
        try {
            return bytesToHex(MessageDigest.getInstance("MD5").digest(content.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }

    public static String execute(byte[] bytes) {
        try {
            return bytesToHex(MessageDigest.getInstance("MD5").digest(bytes));
        } catch (Exception e) {
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02x", b & 0xff));
        }
        return hexStringBuilder.toString();
    }

}
