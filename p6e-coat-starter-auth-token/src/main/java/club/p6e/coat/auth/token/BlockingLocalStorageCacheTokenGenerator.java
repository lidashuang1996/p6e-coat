package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.common.utils.GeneratorUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;

/**
 * Blocking Local Storage Cache Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingLocalStorageCacheTokenGenerator implements BlockingTokenGenerator {

    /**
     * Blocking User Token Cache Object
     */
    protected final BlockingUserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Blocking User Token Cache Object
     */
    public BlockingLocalStorageCacheTokenGenerator(BlockingUserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response, User user) {
        final String token = token();
        final int duration = duration();
        final String device = device(request, response);
        cache.set(user.id(), device == null ? "PC" : device, token, user.serialize(), duration);
        return new HashMap<>() {{
            put("token", token);
            put("type", "Bearer");
            put("expiration", duration);
        }};
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
     * Cache Duration
     *
     * @return Cache Duration Number
     */
    public int duration() {
        return 3600;
    }

    /**
     * Token Content
     *
     * @return Token Content
     */
    public String token() {
        return GeneratorUtil.uuid() + System.currentTimeMillis() + GeneratorUtil.random(8, false, false);
    }

}
