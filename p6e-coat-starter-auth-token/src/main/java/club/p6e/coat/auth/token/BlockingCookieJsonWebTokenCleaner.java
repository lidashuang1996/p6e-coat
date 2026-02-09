package club.p6e.coat.auth.token;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;

/**
 * Blocking Cookie JSON Web Token Cleaner
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingCookieJsonWebTokenCleaner implements BlockingTokenCleaner {

    /**
     * Auth Cookie Name
     */
    protected static final String AUTH_COOKIE_NAME = "P6E_AUTH";

    /**
     * JSON Web Token Codec Object
     */
    protected final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param codec JSON Web Token Codec Object
     */
    public BlockingCookieJsonWebTokenCleaner(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                final String name = cookie.getName();
                if (AUTH_COOKIE_NAME.equalsIgnoreCase(name)) {
                    response.addCookie(cookie(name, ""));
                }
            }
        }
        return LocalDateTime.now();
    }

    /**
     * Cookie
     *
     * @param name    Cookie Name
     * @param content Cookie Content
     * @return
     */
    public Cookie cookie(String name, String content) {
        final Cookie cookie = new Cookie(name, content);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        return cookie;
    }

}
