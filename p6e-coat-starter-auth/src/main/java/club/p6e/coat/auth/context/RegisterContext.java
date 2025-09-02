package club.p6e.coat.auth.context;

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
public class RegisterContext implements Serializable {

    /**
     * Register Context / Request
     */
    @Data
    @Accessors(chain = true)
    public static class Request implements Serializable {

        /**
         * Code
         */
        private String code;

        /**
         * Password
         */
        private String password;

        /**
         * Custom Data
         */
        private Map<String, Object> data = new HashMap<>();

    }

    /**
     * Register Context / Dto
     */
    @Data
    @Accessors(chain = true)
    public static class Dto implements Serializable {

        /**
         * Custom Data
         */
        private Map<String, Object> data = new HashMap<>();

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
