package club.p6e.coat.auth.token;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;

/**
 * Blocking Cookie Cache Token Cleaner
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingCookieCacheTokenCleaner implements BlockingTokenCleaner {

    /**
     * Auth Cookie Name
     */
    protected static final String AUTH_COOKIE_NAME = "P6E_AUTH";

    /**
     * User Token Cache Object
     */
    protected final BlockingUserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache User Token Cache Object
     */
    public BlockingCookieCacheTokenCleaner(BlockingUserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                final String name = cookie.getName();
                if (AUTH_COOKIE_NAME.equalsIgnoreCase(cookie.getName())) {
                    response.addCookie(cookie(name, ""));
                    execute(cookie);
                }
            }
        }
        return LocalDateTime.now();
    }

    /**
     * Execute Token Content
     *
     * @param cookie Cookie Object
     */
    public void execute(Cookie cookie) {
        final BlockingUserTokenCache.Model model = cache.getToken(cookie.getValue());
        if (model != null) {
            cache.cleanToken(model.getToken());
        }
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
