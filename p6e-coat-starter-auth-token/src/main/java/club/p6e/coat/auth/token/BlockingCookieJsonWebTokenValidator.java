package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
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
public class BlockingCookieJsonWebTokenValidator implements BlockingTokenValidator {

    /**
     * Auth Cookie Name
     */
    protected static final String AUTH_COOKIE_NAME = "P6E_AUTH";

     /**
     * User Builder Object
     */
    protected final UserBuilder builder;

    /**
     * Json Web Token Codec Object
     */
    protected final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param builder User Builder Object
     * @param codec   Json Web Token Codec Object
     */
    public BlockingCookieJsonWebTokenValidator(UserBuilder builder, JsonWebTokenCodec codec) {
        this.codec = codec;
        this.builder = builder;
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
                        return this.builder.create(content);
                    }
                }
            }
        }
        return null;
    }

}
