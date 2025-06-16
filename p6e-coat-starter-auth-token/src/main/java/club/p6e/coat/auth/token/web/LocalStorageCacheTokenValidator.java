package club.p6e.coat.auth.token.web;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.common.utils.SpringUtil;
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
public class LocalStorageCacheTokenValidator implements TokenValidator {

    /**
     * Bearer Type
     */
    private static final String BEARER_TYPE = "Bearer";

    /**
     * Request Parameter Name
     */
    private static final String REQUEST_PARAMETER_NAME = "token";

    /**
     * Authorization Prefix
     */
    private static final String AUTHORIZATION_PREFIX = BEARER_TYPE + " ";

    /**
     * Authorization Header Name
     */
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    /**
     * User Token Cache Object
     */
    private final UserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache User Token Cache Object
     */
    public LocalStorageCacheTokenValidator(UserTokenCache cache) {
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
                return SpringUtil.getBean(UserBuilder.class).create(content);
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
        UserTokenCache.Model model = null;
        for (final String item : list) {
            if (item.startsWith(AUTHORIZATION_PREFIX)) {
                model = cache.getToken(item.substring(AUTHORIZATION_PREFIX.length()));
            } else {
                model = cache.getToken(item);
            }
            if (model != null) {
                break;
            }
        }
        if (model == null) {
            return null;
        } else {
            return cache.getUser(model.getUid());
        }
    }

}
