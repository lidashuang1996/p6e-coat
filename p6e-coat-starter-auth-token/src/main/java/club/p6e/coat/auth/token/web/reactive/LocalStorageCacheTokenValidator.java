package club.p6e.coat.auth.token.web.reactive;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
    public Mono<User> execute(ServerWebExchange context) {
        final ServerHttpRequest request = context.getRequest();
        final List<String> list = new ArrayList<>();
        final List<String> hList = request.getHeaders().get(AUTHORIZATION_HEADER_NAME);
        final List<String> pList = request.getQueryParams().get(REQUEST_PARAMETER_NAME);
        if (hList != null) {
            list.addAll(hList);
        }
        if (pList != null) {
            list.addAll(pList);
        }
        if (!list.isEmpty()) {
            return execute(new ArrayList<>(list)).flatMap(content -> Mono.just(SpringUtil.getBean(UserBuilder.class).create(content)));
        }
        return Mono.empty();
    }

    /**
     * Execute Token Content
     *
     * @param list Token List Object
     * @return User String Object
     */
    public Mono<String> execute(List<String> list) {
        if (list == null || list.isEmpty()) {
            return Mono.empty();
        } else {
            String token = list.remove(0);
            if (token.startsWith(AUTHORIZATION_PREFIX)) {
                token = token.substring(AUTHORIZATION_PREFIX.length());
            }
            return (token == null || token.isEmpty()) ? execute(list) : cache.getToken(token).flatMap(m -> cache.getUser(m.getUid())).switchIfEmpty(execute(list));
        }
    }

}
