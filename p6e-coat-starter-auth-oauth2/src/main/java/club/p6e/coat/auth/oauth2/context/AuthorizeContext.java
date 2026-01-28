package club.p6e.coat.auth.oauth2.context;

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
public class AuthorizeContext implements Serializable {


        /**
         * Login Context / Authentication / Request
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {

            private String state;
            private String scope;
            private String clientId;
            private String redirectUri;
            private String responseType;

            final String state = request.getState();
            final String scope = request.getScope();
            final String clientId = request.getClientId();
            final String redirectUri = request.getRedirectUri();

            /**
             * Custom Data
             */
            private Map<String, String> data = new HashMap<>();

        }

        /**
         * Login Context / Authentication / Dto
         */
        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {
        }


    /**
     * Login Context / Account Password
     */
    public static class AccountPassword implements Serializable {

        /**
         * Login Context / Account Password / Request
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
         * Login Context / Verification Code / Request
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
         * Login Context / Verification Code Acquisition / Request
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
         * Login Context / Verification Code Acquisition / Dto
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
         * Login Context / Quick Response Code / Request
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
         * Login Context / Quick Response Code / Dto
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

    /**
     * Login Context / Quick Response Code Acquisition
     */
    public static class QuickResponseCodeAcquisition implements Serializable {

        /**
         * Login Context / Quick Response Code Acquisition / Request
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {

            /**
             * Custom Data
             */
            private Map<String, Object> data = new HashMap<>();

        }

        /**
         * Login Context / Quick Response Code Acquisition / Dto
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

    /**
     * Login Context / Quick Response Code Callback
     */
    public static class QuickResponseCodeCallback implements Serializable {

        /**
         * Login Context / Quick Response Code Callback / Request
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

}
