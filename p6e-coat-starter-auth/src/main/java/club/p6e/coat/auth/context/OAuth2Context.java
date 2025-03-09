package club.p6e.coat.auth.context;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * OAUTH2 的上下文对象
 *
 * @author lidashuang
 * @version 1.0
 */
public class OAuth2Context implements Serializable {

    /**
     * 认证对象
     */
    public static class Auth implements Serializable {

        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {
            private String responseType;
            private String clientId;
            private String redirectUri;
            private String scope;
            private String state;
        }

        @Data
        @Accessors(chain = true)
        public static class Vo implements Serializable {
            private String voucher;
        }

        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {
            private String voucher;
        }

    }

    /**
     * 令牌对象
     */
    public static class Token implements Serializable {

        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {
            private String grantType;
            private String clientId;
            private String clientSecret;
            private String username;
            private String password;
            private String code;
            private String redirectUri;
        }

        @Data
        @Accessors(chain = true)
        public static class Vo implements Serializable {
            private String accessToken;
            private String refreshToken;
            private Long expire;
            private String type;
            private String user;
        }

        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {
            private String accessToken;
            private String refreshToken;
            private Long expire;
            private String type;
            private String user;
        }

        @Data
        @Accessors(chain = true)
        @EqualsAndHashCode(callSuper = true)
        public static class UserDto extends Dto implements Serializable {
            private String openId;
        }

        @Data
        @Accessors(chain = true)
        @EqualsAndHashCode(callSuper = true)
        public static class ClientDto extends Dto implements Serializable {
            private String id;
        }
    }

    /**
     * 二次确认对象
     */
    public static class Confirm implements Serializable {

        /**
         * 请求对象
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {
        }

        /**
         * 结果对象
         */
        @Data
        @Accessors(chain = true)
        public static class Vo implements Serializable {
            private String code;
            private String state;
            private String redirectUri;
        }


        /**
         * 结果对象
         */
        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {
            private String code;
            private String state;
            private String redirectUri;
        }

    }


    public static class UserInfo implements Serializable {

        /**
         * 请求对象
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {
        }

        /**
         * 结果对象
         */
        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {
            private Object data;
        }

    }

    public static class UserTokenLogout implements Serializable {

        /**
         * 请求对象
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {
        }

        /**
         * 结果对象
         */
        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {
            private Object data;
        }

    }

    public static class UserTokenRefresh implements Serializable {

        /**
         * 请求对象
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {
        }

        /**
         * 结果对象
         */
        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {
            private String accessToken;
            private String refreshToken;
            private Long expiration;
        }

    }

    public static class ClientInfo implements Serializable {

        /**
         * 请求对象
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {
        }

        /**
         * 结果对象
         */
        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {
            private Object data;
        }

    }

    public static class ClientTokenLogout implements Serializable {

        /**
         * 请求对象
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {
        }

        /**
         * 结果对象
         */
        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {
            private Object data;
        }

    }

    public static class ClientTokenRefresh implements Serializable {

        /**
         * 请求对象
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {
        }

        /**
         * 结果对象
         */
        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {
            private String accessToken;
            private String refreshToken;
            private Long expiration;
        }

    }

}
