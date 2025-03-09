package club.p6e.coat.auth;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.MediaType;

import java.io.Serializable;

/**
 * 认证相关页面
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class AuthPage {

    /**
     * ME PAGE
     */
    private static final Model ME = new Model("@{page}");

    /**
     * LOGIN PAGE
     */
    private static final Model LOGIN = new Model("@{page}");

    /**
     * REGISTER PAGE
     */
    private static final Model REGISTER = new Model("@{page}");

    /**
     * OAUTH2 CONFIRM PAGE
     */
    private static final Model OAUTH2_LOGIN = new Model("@{page}");

    /**
     * OAUTH2 CONFIRM PAGE
     */
    private static final Model OAUTH2_CONFIRM = new Model("@{page}");

    /**
     * FORGOT PASSWORD PAGE
     */
    private static final Model FORGOT_PASSWORD = new Model("@{page}");

    /**
     * ME PAGE
     *
     * @return 我的页面
     */
    public static Model me() {
        return ME;
    }

    /**
     * SET ME PAGE
     *
     * @param content 内容
     */
    public static void setMe(String content) {
        ME.setContent(content);
    }

    /**
     * SET ME PAGE
     *
     * @param type    类型
     * @param content 内容
     */
    public static void setMe(MediaType type, String content) {
        ME.setType(type);
        ME.setContent(content);
    }

    /**
     * LOGIN
     *
     * @return 登录页面
     */
    public static Model login() {
        return LOGIN;
    }

    /**
     * SET LOGIN PAGE
     *
     * @param content 内容
     */
    public static void setLogin(String content) {
        LOGIN.setContent(content);
    }

    /**
     * SET LOGIN PAGE
     *
     * @param type    类型
     * @param content 内容
     */
    public static void setLogin(MediaType type, String content) {
        LOGIN.setType(type);
        LOGIN.setContent(content);
    }

    /**
     * REGISTER
     *
     * @return 注册页面
     */
    public static Model register() {
        return REGISTER;
    }

    /**
     * SET REGISTER PAGE
     *
     * @param content 内容
     */
    public static void setRegister(String content) {
        REGISTER.setContent(content);
    }

    /**
     * SET REGISTER PAGE
     *
     * @param type    类型
     * @param content 内容
     */
    public static void setRegister(MediaType type, String content) {
        REGISTER.setType(type);
        REGISTER.setContent(content);
    }

    /**
     * FORGOT PASSWORD
     *
     * @return 忘记密码页面
     */
    public static Model forgotPassword() {
        return FORGOT_PASSWORD;
    }

    /**
     * SET FORGOT PASSWORD PAGE
     *
     * @param content 内容
     */
    public static void setForgotPassword(String content) {
        FORGOT_PASSWORD.setContent(content);
    }

    /**
     * SET FORGOT PASSWORD PAGE
     *
     * @param type    类型
     * @param content 内容
     */
    public static void setForgotPassword(MediaType type, String content) {
        FORGOT_PASSWORD.setType(type);
        FORGOT_PASSWORD.setContent(content);
    }

    /**
     * OAUTH2 LOGIN
     *
     * @return OAUTH2 LOGIN PAGE
     */
    public static Model oAuth2Login() {
        return OAUTH2_LOGIN;
    }

    /**
     * SET OAUTH2 LOGIN PAGE
     *
     * @param content 内容
     */
    public static void setOAuth2Login(String content) {
        OAUTH2_LOGIN.setContent(content);
    }

    /**
     * SET OAUTH2 LOGIN PAGE
     *
     * @param type    类型
     * @param content 内容
     */
    public static void setOAuth2Login(MediaType type, String content) {
        OAUTH2_LOGIN.setType(type);
        OAUTH2_LOGIN.setContent(content);
    }

    /**
     * OAUTH2 CONFIRM
     *
     * @return OAUTH2 CONFIRM PAGE
     */
    public static Model oAuth2Confirm() {
        return OAUTH2_CONFIRM;
    }

    /**
     * SET OAUTH2 CONFIRM PAGE
     *
     * @param content 内容
     */
    public static void setOAuth2Confirm(String content) {
        OAUTH2_CONFIRM.setContent(content);
    }

    /**
     * SET OAUTH2 CONFIRM PAGE
     *
     * @param type    类型
     * @param content 内容
     */
    public static void setOAuth2Confirm(MediaType type, String content) {
        OAUTH2_CONFIRM.setType(type);
        OAUTH2_CONFIRM.setContent(content);
    }

    /**
     * 模型类
     */
    @Data
    @Accessors(chain = true)
    public static class Model implements Serializable {

        /**
         * 类型
         */
        private MediaType type;

        /**
         * 内容
         */
        private String content;

        /**
         * 构造方法
         *
         * @param content 内容
         */
        public Model(String content) {
            this.content = content;
            this.type = MediaType.TEXT_HTML;
        }

        /**
         * 构造方法
         *
         * @param type    类型
         * @param content 内容
         */
        public Model(MediaType type, String content) {
            this.type = type;
            this.content = content;
        }
    }
}
