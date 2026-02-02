package club.p6e.coat.auth.oauth2.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Token Context
 *
 * @author lidashuang
 * @version 1.0
 */
public class TokenContext implements Serializable {

    /**
     * Token Context / Request
     */
    @Data
    @Accessors(chain = true)
    public static class Request implements Serializable {

        /**
         * Code
         */
        private String code;

        /**
         * Scope
         */
        private String scope;

        /**
         * Client ID
         */
        private String clientId;

        /**
         * Grant Type
         */
        private String grantType;

        /**
         * Redirect URI
         */
        private String redirectUri;

        /**
         * Client Secret
         */
        private String clientSecret;

        /**
         * Username
         */
        private String username;

        /**
         * Password
         */
        private String password;

    }

}
