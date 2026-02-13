package club.p6e.coat.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Sha256 Util
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class Sha256Util {

    /**
     * Definition
     */
    public interface Definition {

        /**
         * Execute
         *
         * @param bytes Byte Array Object
         * @return Byte Array Object
         */
        byte[] execute(byte[] bytes);

        /**
         * Execute
         *
         * @return Sha256 Message Digest Object
         */
        MessageDigest execute();

    }

    /**
     * Implementation
     */
    private static class Implementation implements Definition {

        @Override
        public byte[] execute(byte[] content) {
            try {
                return MessageDigest.getInstance("SHA-256").digest(content);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public MessageDigest execute() {
            try {
                return MessageDigest.getInstance("SHA-256");
            } catch (Exception e) {
                return null;
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
     * Execute
     *
     * @return Message Digest Object
     */
    public static MessageDigest execute() {
        return DEFINITION.execute();
    }

    /**
     * Execute
     *
     * @param bytes Byte Array Object
     * @return Byte Array Object
     */
    public static byte[] execute(byte[] bytes) {
        return DEFINITION.execute(bytes);
    }

    /**
     * Execute
     *
     * @param bytes Byte Array Object
     * @return Hex String
     */
    public static String executeToHex(byte[] bytes) {
        return HexFormat.of().formatHex(DEFINITION.execute(bytes));
    }

    /**
     * Execute
     *
     * @param bytes Byte Array Object
     * @return Base64 String
     */
    public static String executeToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(DEFINITION.execute(bytes));
    }

    /**
     * Execute
     *
     * @param content Content Data
     * @return Byte Array Object
     */
    public static byte[] execute(String content) {
        return DEFINITION.execute(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Execute
     *
     * @param content Content String
     * @return Hex String
     */
    public static String executeToHex(String content) {
        return HexFormat.of().formatHex(DEFINITION.execute(content.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Execute
     *
     * @param content Content String
     * @return Base64 String
     */
    public static String executeToBase64(String content) {
        return Base64.getEncoder().encodeToString(DEFINITION.execute(content.getBytes(StandardCharsets.UTF_8)));
    }

}
