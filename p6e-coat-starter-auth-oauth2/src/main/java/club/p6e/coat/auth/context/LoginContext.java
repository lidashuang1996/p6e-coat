package club.p6e.coat.auth.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Login Context
 *
 * @author lidashuang
 * @version 1.0
 */
public class LoginContext implements Serializable {

    /**
     * Login Context / Authentication
     */
    public static class Authentication implements Serializable {

        /**
         * Request
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {

            /**
             * Custom Data
             */
            private Map<String, Object> data = new HashMap<>();

        }

    }

    /**
     * Login Context / Account Password
     */
    public static class AccountPassword implements Serializable {

        /**
         * Request
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {

            /**
             * Account
             */
            private String account;

            /**
             * Password
             */
            private String password;

            /**
             * Custom Data
             */
            private Map<String, Object> data = new HashMap<>();

        }

    }

    /**
     * Login Context / Verification Code
     */
    public static class VerificationCode implements Serializable {

        /**
         * Request
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {

            /**
             * Code
             */
            private String code;

            /**
             * Custom Data
             */
            private Map<String, Object> data = new HashMap<>();

        }

    }

    /**
     * Login Context / Verification Code Acquisition
     */
    public static class VerificationCodeAcquisition implements Serializable {

        /**
         * Request
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {

            /**
             * Account
             */
            private String account;

            /**
             * Language
             */
            private String language;

            /**
             * Custom Data
             */
            private Map<String, Object> data = new HashMap<>();

        }

        /**
         * Dto
         */
        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {

            /**
             * Account
             */
            private String account;

        }

    }

    /**
     * Login Context / Quick Response Code
     */
    public static class QuickResponseCode implements Serializable {

        /**
         * Request
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {

            /**
             * Content
             */
            private String content;

            /**
             * Custom Data
             */
            private Map<String, Object> data = new HashMap<>();

        }

    }

    /**
     * Login Context / Quick Response Code Acquisition
     */
    public static class QuickResponseCodeAcquisition implements Serializable {

        /**
         * Request
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {

            /**
             * Content
             */
            private String content;

            /**
             * Custom Data
             */
            private Map<String, Object> data = new HashMap<>();

        }

        /**
         * Dto
         */
        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {

            /**
             * Quick Response Code Content
             */
            private String content;

        }

    }

    /**
     * Login Context / Quick Response Code Callback
     */
    public static class QuickResponseCodeCallback implements Serializable {

        /**
         * Request
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {

            /**
             * Content
             */
            private String content;

            /**
             * Custom Data
             */
            private Map<String, Object> data = new HashMap<>();

        }

        /**
         * Dto
         */
        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {

            /**
             * Content
             */
            private String content;

        }

    }

}
