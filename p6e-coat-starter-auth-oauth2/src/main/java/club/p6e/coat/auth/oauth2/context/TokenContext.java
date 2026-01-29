package club.p6e.coat.auth.oauth2.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Register Context
 *
 * @author lidashuang
 * @version 1.0
 */
public class TokenContext implements Serializable {

    /**
     * Register Context / Request
     */
    @Data
    @Accessors(chain = true)
    public static class Request implements Serializable {

        /**
         * Code
         */
        private String grantType;
        private String clientId;
        private String clientSecret;
        private String username;
        private String password;
        private String code;
        private String redirectUri;
        private String scope;

    }

    /**
     * Register Context / Dto
     */
    @Data
    @Accessors(chain = true)
    public static class Dto implements Serializable {
        private String oid;
        private String type;
        private String user;
        private String token;
        private Long expiration;
    }

    /**
     * Register Context / Verification Code Acquisition
     */
    public static class VerificationCodeAcquisition implements Serializable {

        /**
         * Register Context / Verification Code Acquisition / Request
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
            private String language;

            /**
             * Custom Data
             */
            private Map<String, Object> data = new HashMap<>();

        }

        /**
         * Register Context / Verification Code Acquisition / Dto
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

}
