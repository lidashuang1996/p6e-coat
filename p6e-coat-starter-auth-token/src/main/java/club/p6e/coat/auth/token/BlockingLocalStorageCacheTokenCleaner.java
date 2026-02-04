package club.p6e.coat.auth.token;

import club.p6e.coat.auth.UserBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Blocking Local Storage Cache Token Cleaner
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingLocalStorageCacheTokenCleaner implements BlockingTokenCleaner {

    /**
     * Bearer Type
     */
    protected static final String BEARER_TYPE = "Bearer";

    /**
     * Request Parameter Name
     */
    protected static final String REQUEST_PARAMETER_NAME = "token";

    /**
     * Authorization Prefix
     */
    protected static final String AUTHORIZATION_PREFIX = BEARER_TYPE + " ";

    /**
     * Authorization Header Name
     */
    protected static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    /**
     * User Builder Object
     */
    protected final UserBuilder builder;

    /**
     * User Token Cache Object
     */
    protected final BlockingUserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param builder User Builder Object
     * @param cache   Blocking User Token Cache Object
     */
    public BlockingLocalStorageCacheTokenCleaner(UserBuilder builder, BlockingUserTokenCache cache) {
        this.cache = cache;
        this.builder = builder;
    }

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response) {
        final List<String> list = new ArrayList<>();
        final String ht = request.getHeader(AUTHORIZATION_HEADER_NAME);
        final String qt = request.getParameter(REQUEST_PARAMETER_NAME);
        if (ht != null) {
            list.add(ht);
        }
        if (qt != null) {
            list.add(qt);
        }
        if (!list.isEmpty()) {
            execute(list);
        }
        return LocalDateTime.now();
    }

    /**
     * Execute Token Content
     *
     * @param list Token List Object
     */
    public void execute(List<String> list) {
        for (final String item : list) {
            final BlockingUserTokenCache.Model model;
            if (item.startsWith(AUTHORIZATION_PREFIX)) {
                model = cache.getToken(item.substring(AUTHORIZATION_PREFIX.length()));
            } else {
                model = cache.getToken(item);
            }
            if (model != null) {
                cache.cleanToken(item);
            }
        }
    }

}
