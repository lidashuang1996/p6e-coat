package club.p6e.coat.auth.token.web;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.common.utils.SpringUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Cookie Cache Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class CookieCacheTokenValidator implements TokenValidator {

    /**
     * Auth Cookie Name
     */
    private static final String AUTH_COOKIE_NAME = "P6E_AUTH";

    /**
     * User Token Cache Object
     */
    private final UserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache User Token Cache Object
     */
    public CookieCacheTokenValidator(UserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public User execute(HttpServletRequest request, HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (AUTH_COOKIE_NAME.equalsIgnoreCase(cookie.getName())) {
                    final String content = execute(cookie);
                    if (content != null) {
                        return SpringUtil.getBean(UserBuilder.class).create(content);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Execute Token Content
     *
     * @param cookie Cookie Object
     * @return User String Object
     */
    public String execute(Cookie cookie) {
        final UserTokenCache.Model model = cache.getToken(cookie.getValue());
        if (model != null) {
            return cache.getUser(model.getUid());
        }
        return null;
    }

}
