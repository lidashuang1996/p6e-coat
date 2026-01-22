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
    public BlockingLocalStorageCacheTokenGenerator(BlockingUserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response, User user) {
        final String token = token();
        final long duration = duration();
        final String device = request.getHeader(DEVICE_HEADER_NAME);
        cache.set(user.id(), device == null ? "PC" : device, token, user.serialize(), duration);
        return new HashMap<>() {{
            put("token", token);
            put("type", "Bearer");
            put("expiration", duration);
        }};
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
        return GeneratorUtil.uuid() + GeneratorUtil.random(8, false, false);
    }
    
}
