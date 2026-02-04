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
                if (AUTH_COOKIE_NAME.equalsIgnoreCase(cookie.getName())) {
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

}
