package club.p6e.coat.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.MediaType;
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
     * Get Properties Instance
     *
     * @return Properties Object
     */
    public static Properties getInstance() {
        return INSTANCE;
    }

    /**
     * Private Constructors
     */
    private Properties() {
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
     * Token
     */
    private Token token = new Token();

    /**
     * Enable
     */
    private boolean enable = true;

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
     * Authentication Mode
     */
    private Mode mode = Mode.PHONE_OR_MAILBOX;

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Page implements Serializable {
        private String type;
        private String content;
    }

    @Data
    @Accessors(chain = true)
    public static class Login implements Serializable {
        /**
         * 是否开启登录功能
         */
        private boolean enable = true;

        /**
         * 页面
         */
        private Page page = new Page(
                "application/javascript",
                "window.p6e_v='@{VOUCHER}';"
        );

        /**
         * 账号密码登录的配置
         */
        private AccountPassword accountPassword = new AccountPassword();


        @Data
        @Accessors(chain = true)
        public static class AccountPassword implements Serializable {
            /**
             * 是否开启账号密码登录功能
             */
            private boolean enable = true;

            /**
             * 开启账号密码登录时候是否对密码进行加密
             */
            private boolean enableTransmissionEncryption = true;
        }

        /**
         * 验证码登录的配置
         */
        private VerificationCode verificationCode = new VerificationCode();

        @Data
        @Accessors(chain = true)
        public static class VerificationCode implements Serializable {
            /**
             * 是否开启验证码登录功能
             * 开启验证码登录功能，账号模式需要为手机模式或邮箱模式或手机或者邮箱模式
             */
            private boolean enable = true;
        }

        /**
         * 二维码登录的配置
         */
        private QuickResponseCode quickResponseCode = new QuickResponseCode();

        @Data
        @Accessors(chain = true)
        public static class QuickResponseCode implements Serializable {
            /**
             * 是否开启二维码扫码登录功能
             */
            private boolean enable = true;
        }

        /**
         * 其它第三方登录的配置
         */
        private Map<String, Other> others = new HashMap<>();

        @Data
        public static class Other implements Serializable {
            /**
             * 是否开启此第三方登录功能
             */
            private boolean enable = false;

            /**
             * 此第三方登录需要的配置对象
             */
            private Map<String, String> config = new HashMap<>();
        }

        public Login() {
            final Map<String, String> map = new HashMap<>();
            map.put("key", "my_key");
            map.put("secret", "my_secret");
            map.put("redirect_uri", "my_redirect_uri");
            map.put("#home_response_type", "code");
            map.put("#home_scope", "get_user_info");
            map.put("#home_client_id", "@{key}");
            map.put("#home_redirect_uri", "@{redirect_uri}");
            map.put("@home", "https://graph.qq.com/oauth2.0/authorize"
                    + "?response_type=@{#home_response_type}"
                    + "&client_id=@{#home_client_id}"
                    + "&redirect_uri=@{#home_redirect_uri}"
                    + "&scope=@{#home_scope}"
            );
            map.put("#token_client_id", "@{key}");
            map.put("#token_client_secret", "@{secret}");
            map.put("#token_redirect_uri", "@{redirect_uri}");
            map.put("#token_grant_type", "authorization_code");
            map.put("@token", "https://graph.qq.com/oauth2.0/token"
                    + "?grant_type=@{#token_grant_type}"
                    + "&client_id=@{#token_client_id}"
                    + "&client_secret=@{#token_client_secret}"
                    + "&redirect_uri=@{#token_redirect_uri}"
            );
            map.put("#info_oauth_consumer_key", "@{key}");
            map.put("@me", "https://graph.qq.com/oauth2.0/me");
            map.put("@info", "https://graph.qq.com/user/get_user_info?oauth_consumer_key=@{#info_oauth_consumer_key}");
            others.put("QQ", new Other().setEnable(false).setConfig(map));
        }
    }

    /**
     * Login
     */
    private Login login = new Login();

    @Data
    @Accessors(chain = true)
    public static class Register implements Serializable {

        /**
         * Enable
         */
        private boolean enable = false;

        /**
         * Enable Other Login Binding
         * register and bind when there is no
         * corresponding binding information for third-party login
         */
        private boolean enableOtherLoginBinding = false;

    }

    /**
     * Register
     */
    private Register register = new Register();

    @Data
    @Accessors(chain = true)
    public static class ForgotPassword implements Serializable {

        /**
         * Enable
         */
        private boolean enable = false;

    }

    /**
     * Forgot Password
     */
    private ForgotPassword forgotPassword = new ForgotPassword();

}
