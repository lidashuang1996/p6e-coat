package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Blocking Cookie Cache Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingCookieCacheTokenValidator implements BlockingTokenValidator {

    /**
     * User Builder Object
     */
    protected final UserBuilder builder;

    /**
     * Blocking User Token Cache Object
     */
    protected final BlockingUserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param builder User Builder Object
     * @param cache   Blocking User Token Cache Object
     */
    public BlockingCookieCacheTokenValidator(UserBuilder builder, BlockingUserTokenCache cache) {
        this.cache = cache;
        this.builder = builder;
    }

    @Override
    public User execute(HttpServletRequest request, HttpServletResponse response) {
        final String name = name();
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (name.equalsIgnoreCase(cookie.getName())) {
                    final BlockingUserTokenCache.Model model = cache.getToken(cookie.getValue());
                    if (model != null) {
                        final String content = cache.getUser(model.getUid());
                        if (content != null) {
                            return this.builder.create(content);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Name
     *
     * @return Name
     */
    public String name() {
        return "P6E_AUTH";
    }

}
