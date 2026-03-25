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
     * Blocking User Token Cache Object
     */
    protected final BlockingUserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Blocking User Token Cache Object
     */
    public BlockingCookieCacheTokenCleaner(BlockingUserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response) {
        final String name = name();
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (name.equalsIgnoreCase(cookie.getName())) {
                    response.addCookie(cookie(cookie.getName(), ""));
                    final BlockingUserTokenCache.Model model = cache.getToken(cookie.getValue());
                    if (model != null) {
                        cache.cleanToken(model.getToken());
                    }
                }
            }
        }
        return LocalDateTime.now();
    }

    /**
     * Get Cookie Name
     *
     * @return Cookie Name
     */
    public String name() {
        return "P6E_AUTH";
    }

    /**
     * Set Cookie
     *
     * @param name    Cookie Name
     * @param content Cookie Content
     * @return Cookie Object
     */
    public Cookie cookie(String name, String content) {
        final Cookie cookie = new Cookie(name, content);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        return cookie;
    }

}
