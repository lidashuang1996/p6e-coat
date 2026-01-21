package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Local Storage Cache Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingLocalStorageCacheTokenValidator implements BlockingTokenValidator {

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
     * @param cache   User Token Cache Object
     */
    public BlockingLocalStorageCacheTokenValidator(UserBuilder builder, BlockingUserTokenCache cache) {
        this.builder = builder;
        this.cache = cache;
    }

    @Override
    public User execute(HttpServletRequest request, HttpServletResponse response) {
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
            final String content = execute(list);
            if (content != null) {
                return builder.create(content);
            }
        }
        return null;
    }

    /**
     * Execute Token Content
     *
     * @param list Token List Object
     * @return User String Object
     */
    public String execute(List<String> list) {
        String result = null;
        for (final String item : list) {
            final BlockingUserTokenCache.Model model;
            if (item.startsWith(AUTHORIZATION_PREFIX)) {
                model = cache.getToken(item.substring(AUTHORIZATION_PREFIX.length()));
            } else {
                model = cache.getToken(item);
            }
            if (model != null) {
                result = cache.getUser(model.getUid());
            }
            if (result != null) {
                break;
            }
        }
        return result;
    }

}
