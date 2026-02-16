package club.p6e.coat.common.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generator Util
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class GeneratorUtil {

    /**
     * Definition
     */
    public interface Definition {

        /**
         * Generate UUID
         *
         * @return UUID
         */
        String uuid();

        /**
         * Generate Random String
         *
         * @param len      Data Length
         * @param isLetter Data Contains Letter
         * @param isCase   Data Contains Case
         * @return Random String
         */
        String random(int len, boolean isLetter, boolean isCase);

        /**
         * Generate Password
         *
         * @param len    Password Length
         * @param source Password Source
         * @return Password Length Content
         */
        String password(int len, char[] source);

        /**
         * Generate Password
         *
         * @param len Password Length
         * @return Password Length Content
         */
        default String password(int len) {
            return password(len, new char[]{
                    '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm',
                    'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
                    'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                    '!', '@', '#', '$', '%', '^', '&', '*', '+', '-'
            });
        }

    }

    /**
     * Implementation
     */
    private static class Implementation implements Definition {

        /**
         * Base Char Data
         */
        private static final String[] BASE_DATA = new String[]{
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
                "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        };

        @Override
        public String uuid() {
            return UUID.randomUUID().toString().replaceAll("-", "");
        }

        @Override
        public String random(int len, boolean isLetter, boolean isCase) {
            final StringBuilder sb = new StringBuilder();
            final int base = isLetter ? (isCase ? 62 : 36) : 10;
            for (int i = 0; i < len; i++) {
                sb.append(BASE_DATA[ThreadLocalRandom.current().nextInt(base)]);
            }
            return sb.toString();
        }

        @Override
        public String password(int len, char[] source) {
            if (source == null || source.length == 0) {
                return random(len, true, true);
            }
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) {
                sb.append(source[ThreadLocalRandom.current().nextInt(source.length)]);
            }
            return sb.toString();
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
     * Generate UUID
     *
     * @return UUID
     */
    public static String uuid() {
        return DEFINITION.uuid();
    }

    /**
     * Generate Random 6 Number String
     *
     * @return Random 6 Number String
     */
    public static String random() {
        return DEFINITION.random(6, false, false);
    }

    /**
     * Generate Random String
     *
     * @param len      Data Length
     * @param isLetter Data Contains Letter
     * @param isCase   Data Contains Case
     * @return Random String
     */
    public static String random(int len, boolean isLetter, boolean isCase) {
        return DEFINITION.random(len, isLetter, isCase);
    }

    /**
     * Generate Password
     *
     * @param len Password Length
     * @return Password Length Content
     */
    public static String password(int len) {
        return DEFINITION.password(len);
    }

    /**
     * Generate Password
     *
     * @param len    Password Length
     * @param source Password Source
     * @return Password Length Content
     */
    public static String password(int len, char[] source) {
        return DEFINITION.password(len, source);
    }

}
