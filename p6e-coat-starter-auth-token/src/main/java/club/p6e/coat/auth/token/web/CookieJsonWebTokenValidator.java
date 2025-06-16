package club.p6e.coat.auth.token.web;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.token.JsonWebTokenCodec;
import club.p6e.coat.common.utils.SpringUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Cookie Json Web Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class CookieJsonWebTokenValidator implements TokenValidator {

    /**
     * Auth Cookie Name
     */
    private static final String AUTH_COOKIE_NAME = "P6E_AUTH";

    /**
     * Json Web Token Codec Object
     */
    private final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param codec Json Web Token Codec Object
     */
    public CookieJsonWebTokenValidator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public User execute(HttpServletRequest request, HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (AUTH_COOKIE_NAME.equalsIgnoreCase(cookie.getName())) {
                    String content = codec.decryption(cookie.getValue());
                    if (content != null) {
                        content = content.substring(content.indexOf("@") + 1);
                        return SpringUtil.getBean(UserBuilder.class).create(content);
                    }
                }
            }
        }
        return null;
    }

}
