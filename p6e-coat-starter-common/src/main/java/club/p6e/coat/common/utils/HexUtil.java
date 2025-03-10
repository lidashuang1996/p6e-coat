package club.p6e.coat.common.utils;

/**
 * @author lidashuang
 * @version 1.0
 */
public final class HexUtil {

    public static String bytesToHex(byte[] bytes) {
        final StringBuilder result = new StringBuilder();
        for (final byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    public static byte[] hexToBytes(String content) {
        int length = content.length();
        byte[] result = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            result[i / 2] = (byte) (
                    (Character.digit(content.charAt(i), 16) << 4)
                            + Character.digit(content.charAt(i + 1), 16)
            );
        }
        return result;
    }

}
