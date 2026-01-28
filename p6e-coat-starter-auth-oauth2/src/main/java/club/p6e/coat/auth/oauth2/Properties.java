package club.p6e.coat.auth.oauth2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Properties
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class Properties implements Serializable {

    /**
     * Properties Instance Object
     */
    private static Properties INSTANCE = new Properties();

    /**
     * Enable
     */
    private boolean enable = true;

    /**
     * Authentication Mode
     */
    private Mode mode = Mode.PHONE_OR_MAILBOX;

    /**
     * Token
     */
    private Token token = new Token();

    /**
     * Login
     */
    private AuthorizationCode authorizationCode = new AuthorizationCode();

    /**
     * Register
     */
    private Register register = new Register();

    /**
     * Forgot Password
     */
    private ForgotPassword forgotPassword = new ForgotPassword();

    /**
     * Private Constructors
     */
    private Properties() {
    }

    /**
     * Get Properties Instance
     *
     * @return Properties Object
     */
    public static Properties getInstance() {
        return INSTANCE;
    }

    /**
     * Mode
     */
    public enum Mode implements Serializable {
        /**
         * PHONE Mode
         */
        PHONE,

        /**
         * MAILBOX Mode
         */
        MAILBOX,

        /**
         * ACCOUNT Mode
         */
        ACCOUNT,

        /**
         * PHONE_OR_MAILBOX Mode
         */
        PHONE_OR_MAILBOX;

        /**
         * Create Mode Object
         *
         * @param mode Mode Content
         * @return Mode Object
         */
        public static Mode create(String mode) {
            if (StringUtils.hasText(mode)) {
                return switch (mode.toUpperCase()) {
                    case "PHONE" -> PHONE;
                    case "MAILBOX" -> MAILBOX;
                    case "ACCOUNT" -> ACCOUNT;
                    default -> PHONE_OR_MAILBOX;
                };
            } else {
                return PHONE_OR_MAILBOX;
            }
        }
    }

    /**
     * Token
     */
    @Data
    @Accessors(chain = true)
    public static class Token {

        /**
         * Token Duration -> Default Value 3600 (S) * 3
         */
        private Duration duration = Duration.ofSeconds(3600 * 3L);

    }

    /**
     * Page
     */
    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Page implements Serializable {

        /**
         * Page Type
         */
        private String type;

        /**
         * Page Content
         */
        private String content;

    }

    /**
     * Login
     */
    @Data
    @Accessors(chain = true)
    public static class AuthorizationCode implements Serializable {

        /**
         * Login Enable
         */
        private boolean enable = true;

        /**
         * Login Page
         */
        private Page page = new Page(
                "application/javascript",
                "window.voucher='@{VOUCHER}';"
        );

        /**
         * Login Account Password
         */
        private AccountPassword accountPassword = new AccountPassword();

        /**
         * Login Verification Code
         */
        private VerificationCode verificationCode = new VerificationCode();

        /**
         * Login Quick Response Code
         */
        private QuickResponseCode quickResponseCode = new QuickResponseCode();

        /**
         * Login Others
         */
        private Map<String, Other> others = new HashMap<>();

        /**
         * Login Account Password
         */
        @Data
        @Accessors(chain = true)
        public static class AccountPassword implements Serializable {

            /**
             * Login Account Password Enable
             */
            private boolean enable = true;

            /**
             * Login Account Password Enable Transmission Encryption
             */
            private boolean enableTransmissionEncryption = true;

        }

        /**
         * Login Verification Code
         */
        @Data
        @Accessors(chain = true)
        public static class VerificationCode implements Serializable {

            /**
             * Login Verification Code Enable
             */
            private boolean enable = true;

        }

        /**
         * Login Quick Response Code
         */
        @Data
        @Accessors(chain = true)
        public static class QuickResponseCode implements Serializable {

            /**
             * Login Quick Response Code Enable
             */
            private boolean enable = true;

        }

        /**
         * Login Other
         */
        @Data
        @Accessors(chain = true)
        public static class Other implements Serializable {

            /**
             * Login Other Enable
             */
            private boolean enable = false;

            /**
             * Login Other Config
             */
            private Map<String, String> config = new HashMap<>();

        }

    }

    /**
     * Register
     */
    @Data
    @Accessors(chain = true)
    public static class Register implements Serializable {

        /**
         * Register Enable
         */
        private boolean enable = false;

    }

    /**
     * Forgot Password
     */
    @Data
    @Accessors(chain = true)
    public static class ForgotPassword implements Serializable {

        /**
         * Forgot Password Enable
         */
        private boolean enable = false;

    }

}
