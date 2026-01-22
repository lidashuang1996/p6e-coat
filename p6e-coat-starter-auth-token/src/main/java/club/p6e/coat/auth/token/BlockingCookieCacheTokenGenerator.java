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
     * Auth Cookie Name
     */
    protected static final String AUTH_COOKIE_NAME = "P6E_AUTH";

    /**
     * Device Header Name
     * Request Header Of the Current Device
     * Request Header Is Customized By The Program And Not Carried By The User Request
     * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
     */
    @SuppressWarnings("ALL")
    protected static final String DEVICE_HEADER_NAME = "P6e-Device";

    /**
     * User Token Cache Object
     */
    protected final BlockingUserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache User Token Cache Object
     */
    public BlockingCookieCacheTokenGenerator(BlockingUserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response, User user) {
        final String token = token();
        final long duration = duration();
        final String device = request.getHeader(DEVICE_HEADER_NAME);
        cache.set(user.id(), device == null ? "PC" : device, token, user.serialize(), duration);
        response.addCookie(cookie(AUTH_COOKIE_NAME, token));
        return LocalDateTime.now();
    }

    /**
     * Cache Duration
     *
     * @return Cache Duration Number
     */
    public long duration() {
        return 3600L;
    }

    /**
     * Token Content
     *
     * @return Token Content
     */
    public String token() {
        return GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
    }

    /**
     * Cookie
     *
     * @param name    Cookie Name
     * @param content Cookie Content
     * @return
     */
    public Cookie cookie(String name, String content) {
        final int age = (int) duration();
        final Cookie cookie = new Cookie(name, content);
        cookie.setPath("/");
        cookie.setMaxAge(age);
        cookie.setHttpOnly(true);
        return cookie;
    }

}
