package club.p6e.coat.common.utils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class VerificationUtil {

    public static boolean validationCode(String code) {
        return code.length() == 6;
    }

    public static boolean validationPassword(String password) {
        return password.length() >= 6;
    }

    /**
     * 定义类
     */
    public interface Definition {

        /**
         * 验证手机号码
         *
         * @param content 待验证的内容
         * @return 是否为手机号码
         */
        public boolean validationPhone(String content);

        /**
         * 验证邮箱
         *
         * @param content 待验证的内容
         * @return 是否为邮箱
         */
        public boolean validationMailbox(String content);

        /**
         * 验证 OAuth2 作用域
         *
         * @param source  源
         * @param content 内容
         * @return 验证结果
         */
        public boolean validationOAuth2Scope(String source, String content);

        /**
         * 验证 OAuth2 重定向 URI
         *
         * @param source  源
         * @param content 内容
         * @return 验证结果
         */
        public boolean validationOAuth2RedirectUri(String source, String content);
    }

    /**
     * 实现类
     */
    public static class Implementation implements Definition {

        @Override
        public boolean validationPhone(String content) {
            return Pattern.matches("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", content);
        }

        @Override
        public boolean validationMailbox(String content) {
            return Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", content);
        }

        @Override
        public boolean validationOAuth2Scope(String source, String content) {
            if (source == null || content == null) {
                return false;
            } else {
                final List<String> sList = List.of(source.split(","));
                final List<String> cList = List.of(content.split(","));
                for (final String ci : cList) {
                    boolean bool = false;
                    for (final String si : sList) {
                        if (si.equalsIgnoreCase(ci)) {
                            bool = true;
                            break;
                        }
                    }
                    if (!bool) {
                        return false;
                    }
                }
                return true;
            }
        }

        @Override
        public boolean validationOAuth2RedirectUri(String source, String content) {
            if (source != null && content != null) {
                final List<String> sList = List.of(source.split(","));
                for (final String si : sList) {
                    if (si.equalsIgnoreCase(content)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 默认的验证实现的对象
     */
    private static Definition DEFINITION = new Implementation();

    /**
     * 设置验证的对象
     *
     * @param implementation 验证的对象
     */
    public static void set(Definition implementation) {
        DEFINITION = implementation;
    }

    /**
     * 验证手机号码
     *
     * @param content 待验证的内容
     * @return 是否为手机号码
     */
    public static boolean validationPhone(String content) {
        return DEFINITION.validationPhone(content);
    }

    /**
     * 验证邮箱
     *
     * @param content 待验证的内容
     * @return 是否为邮箱
     */
    public static boolean validationMailbox(String content) {
        return DEFINITION.validationMailbox(content);
    }

    /**
     * 验证 scope
     *
     * @param source  源
     * @param content 内容
     * @return 验证结果
     */
    public static boolean validationOAuth2Scope(String source, String content) {
        return DEFINITION.validationOAuth2Scope(source, content);
    }

    /**
     * 验证 OAuth2 重定向 URI
     *
     * @param source  源
     * @param content 内容
     * @return 验证结果
     */
    public static boolean validationOAuth2RedirectUri(String source, String content) {
        return DEFINITION.validationOAuth2RedirectUri(source, content);
    }
}
