package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;

/**
 * Cookie Json Web Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingCookieJsonWebTokenGenerator implements BlockingTokenGenerator {

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
        final String device = request.getHeader(DEVICE_HEADER_NAME);
        final String content = codec.encryption(user.id(), (device == null ? "PC" : device) + "@" + user.serialize(), duration);
        final Cookie cookie = new Cookie(AUTH_COOKIE_NAME, content);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) duration);
        response.addCookie(cookie);
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

}
