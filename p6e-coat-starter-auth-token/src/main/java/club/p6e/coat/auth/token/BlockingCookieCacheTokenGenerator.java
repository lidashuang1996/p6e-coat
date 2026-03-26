package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.common.utils.GeneratorUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;

/**
 * Blocking Cookie Cache Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingCookieCacheTokenGenerator implements BlockingTokenGenerator {

    /**
     * Blocking User Token Cache Object
     */
    protected final BlockingUserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Blocking User Token Cache Object
     */
    public BlockingCookieCacheTokenGenerator(BlockingUserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response, User user) {
        final String token = token();
        final long duration = duration();
        final String device = device(request, response);
        cache.set(user.id(), device == null ? "PC" : device, token, user.serialize(), duration);
        response.addCookie(cookie(name(), token));
        return LocalDateTime.now();
    }

    /**
     * Get Device Content
     *
     * @param request  Http Servlet Request Object
     * @param response Http Servlet Response Object
     * @return Device Content
     */
    public String device(HttpServletRequest request, HttpServletResponse response) {
        return request.getHeader("P6e-Device");
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
     * Get Cookie Duration
     *
     * @return Cookie Duration
     */
    public int duration() {
        return 3600;
    }

    /**
     * Get Token Content
     *
     * @return Token Content
     */
    public String token() {
        return GeneratorUtil.uuid() + System.currentTimeMillis() + GeneratorUtil.random(8, true, false);
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
        cookie.setHttpOnly(true);
        cookie.setMaxAge(duration());
        return cookie;
    }

}
