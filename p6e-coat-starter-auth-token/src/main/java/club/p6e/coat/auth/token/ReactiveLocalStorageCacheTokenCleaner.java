package club.p6e.coat.auth.token;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reactive Local Storage Cache Token Cleaner
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveLocalStorageCacheTokenCleaner implements ReactiveTokenCleaner {

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
     * User Token Cache Object
     */
    protected final ReactiveUserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache User Token Cache Object
     */
    public ReactiveLocalStorageCacheTokenCleaner(ReactiveUserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange) {
        final ServerHttpRequest request = exchange.getRequest();
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
            return execute(new ArrayList<>(list)).map(content -> LocalDateTime.now());
        }
        return Mono.just(LocalDateTime.now());
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
            return (token == null || token.isEmpty()) ? execute(list) : this.cache.getToken(token).flatMap(m -> this.cache.cleanToken(m.getToken())).map(m -> m.getToken()).switchIfEmpty(execute(list));
        }
    }

}
