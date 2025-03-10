package club.p6e.coat.common.utils;

/**
 * @author lidashuang
 * @version 1.0
 */
public final class NumberUtil {

    public static byte[] hexToBytes(String hex) {
        if (hex == null || hex.isEmpty()) {
            return null;
        } else {
            final int step = 2;
            final int length = hex.length();
            final byte[] bytes = new byte[length / step];
            for (int i = 0; i < length; i = i + step) {
                try {
                    final String data = hex.substring(i, i + step);
                    bytes[i] = (byte) Integer.parseInt(data, 16);
                } catch (Exception e) {
                    return null;
                }
            }
            return bytes;
        }
    }

}
