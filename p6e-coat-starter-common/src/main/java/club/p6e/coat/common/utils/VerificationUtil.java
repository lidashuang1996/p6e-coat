package club.p6e.coat.common.utils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Verification Util
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class VerificationUtil {

    /**
     * Definition
     */
    public interface Definition {

        /**
         * Validate Phone
         *
         * @param content Phone
         * @return Validate Result
         */
        boolean validatePhone(String content);

        /**
         * Validate Mailbox
         *
         * @param content Mailbox
         * @return Validate Result
         */
        boolean validateMailbox(String content);

        /**
         * Validate String Belong Comma Separated String
         *
         * @param reference Comma Separated String
         * @param source    Source String
         * @return Validate Result
         */
        boolean validateStringBelongCommaSeparatedString(String reference, String source);
    }

    /**
     * Implementation
     */
    public static class Implementation implements Definition {

        @Override
        public boolean validatePhone(String content) {
            return Pattern.matches("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", content);
        }

        @Override
        public boolean validateMailbox(String content) {
            return Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", content);
        }

        @Override
        public boolean validateStringBelongCommaSeparatedString(String reference, String source) {
            if (reference == null || source == null) {
                return false;
            } else {
                final List<String> sList = List.of(source.split(","));
                final List<String> rList = List.of(reference.split(","));
                for (final String s : sList) {
                    boolean bool = false;
                    for (final String r : rList) {
                        if (s.equals(r)) {
                            bool = true;
                            break;
                        }
                    }
                    if (!bool) {
                        return false;
                    }
                }
                return true;
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
     * Validate Phone
     *
     * @param content Phone
     * @return Validate Result
     */
    public static boolean validatePhone(String content) {
        return DEFINITION.validatePhone(content);
    }

    /**
     * Validate Mailbox
     *
     * @param content Mailbox
     * @return Validate Result
     */
    public static boolean validateMailbox(String content) {
        return DEFINITION.validateMailbox(content);
    }

    /**
     * Validate String Belong Comma Separated String
     *
     * @param reference Comma Separated String
     * @param source    Source String
     * @return Validate Result
     */
    public static boolean validateStringBelongCommaSeparatedString(String reference, String source) {
        return DEFINITION.validateStringBelongCommaSeparatedString(reference, source);
    }

}
