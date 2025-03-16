package club.p6e.coat.auth;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 配置文件
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class Properties implements Serializable {

    public static final String P6E_DEVICE_ATTRIBUTE_KEY = "P6e-Device";
    public static final String P6E_USER_AUTH_COOKIE_NAME = "P6e-User-Auth";

    private static Properties INSTANCE = new Properties();

    public static Properties getInstance() {
        return INSTANCE;
    }

    @Data
    @Accessors(chain = true)
    public static class Token {
        private Duration duration = Duration.ofSeconds(3600 * 3L);
    }


    private Token token = new Token();

    /**
     * 是否开启认证服务
     */
    private boolean enable = true;

    /**
     * 对需要认证访问的路径进行拦截
     */
    private String[] interceptor = new String[]{};

    /**
     * 404 页面是否重定向到网站首页
     */
    private boolean redirectIndexPage = false;

    /**
     * 404 页面进行重定向到网站首页的地址路径
     */
    private String redirectIndexPagePath = "/";

    /**
     * 认证的账号模式
     */
    private Mode mode = Mode.ACCOUNT;

    /**
     * 模式
     */
    public enum Mode implements Serializable {
        /**
         * 账号格式为手机
         */
        PHONE,

        /**
         * 账号格式为邮箱
         */
        MAILBOX,

        /**
         * 账号为普通的账号
         */
        ACCOUNT,

        /**
         * 账号为手机或者邮箱
         */
        PHONE_OR_MAILBOX;

        /**
         * 创建模式对象
         *
         * @param mode 模式内容
         * @return 模式对象
         */
        public static Mode structure(String mode) {
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
     * 缓存类型
     */
    private Cache cache = new Cache();

    @Data
    @Accessors(chain = true)
    public static class Cache implements Serializable {
        /**
         * 缓存方式的配置
         */
        private Type type = Type.REDIS;

        /**
         * 缓存枚举类型
         */
        public enum Type implements Serializable {
            REDIS,
            MEMORY
        }
    }

    /**
     * 常见的登录方式配置
     */
    private Login login = new Login();

    @Data
    @Accessors(chain = true)
    public static class Login implements Serializable {
        /**
         * 是否开启登录功能
         */
        private boolean enable = true;

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
            private boolean enableTransmissionEncryption = false;
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
            private boolean enable = false;
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
            private boolean enable = false;
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
     * 常见的 OAUTH2 的配置
     */
    private Oauth2 oauth2 = new Oauth2();

    @Data
    @Accessors(chain = true)
    public static class Oauth2 implements Serializable {
        /**
         * 是否开启 OAUTH2 功能
         */
        private boolean enable = true;

        /**
         * 客户端授权登录的配置
         */
        private Client client = new Client();

        @Data
        @Accessors(chain = true)
        public static class Client implements Serializable {
            /**
             * 是否开启客户端授权登录
             */
            private boolean enable = false;
        }

        /**
         * 密码授权登录的配置
         */
        private Password password = new Password();

        @Data
        @Accessors(chain = true)
        public static class Password implements Serializable {
            /**
             * 是否开启密码授权登录
             */
            private boolean enable = false;
        }

        /**
         * CODE 授权登录的配置
         */
        private AuthorizationCode authorizationCode = new AuthorizationCode();

        @Data
        @Accessors(chain = true)
        public static class AuthorizationCode implements Serializable {
            /**
             * 是否开启 CODE 授权登录
             */
            private boolean enable = true;
        }
    }

    /**
     * 注册配置
     */
    private Register register = new Register();

    @Data
    @Accessors(chain = true)
    public static class Register implements Serializable {
        /**
         * 是否开启注册的功能
         */
        private boolean enable = false;

        /**
         * 是否开启第三方登录没有对应绑定信息时候进行注册绑定
         */
        private boolean enableOtherLoginBinding = false;
    }

    /**
     * 忘记密码配置
     */
    private ForgotPassword forgotPassword = new ForgotPassword();

    @Data
    @Accessors(chain = true)
    public static class ForgotPassword implements Serializable {
        /**
         * 是否开启注册的功能
         */
        private boolean enable = false;
    }

    /**
     * 页面配置
     */
    private Page page = new Page();

    @Data
    @Accessors(chain = true)
    public static class Page implements Serializable {
        private String me;
        private String login;
        private String register;
        private String forgotPassword;
    }

}
