package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;

/**
 * Blocking Cookie Json Web Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingCookieJsonWebTokenGenerator implements BlockingTokenGenerator {

    /**
     * Json Web Token Codec Object
     */
    protected final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param codec Json Web Token Codec Object
     */
    public BlockingCookieJsonWebTokenGenerator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response, User user) {
        final long duration = duration();
        final String device = device(request, response);
        final String content = codec.encryption(user.id(), (device == null ? "PC" : device) + "@" + user.serialize(), duration);
        response.addCookie(cookie(name(), content));
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
     * Cookie
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
