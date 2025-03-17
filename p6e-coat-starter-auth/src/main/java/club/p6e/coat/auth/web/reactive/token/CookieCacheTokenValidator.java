package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.user.User;
import club.p6e.coat.auth.user.UserBuilder;
import club.p6e.coat.auth.web.reactive.cache.UserTokenCache;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Cookie Cache Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
public class CookieCacheTokenValidator implements TokenValidator {

    /**
     * Auth Cookie Name
     */
    private static final String AUTH_COOKIE_NAME = "P6E_AUTH";

    /**
     * User Token Cache Object
     */
    private final UserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache User Token Cache Object
     */
    public CookieCacheTokenValidator(UserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<User> execute(ServerWebExchange context) {
        final ServerHttpRequest request = context.getRequest();
        final MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (!cookies.isEmpty()) {
            for (final String key : cookies.keySet()) {
                if (AUTH_COOKIE_NAME.equalsIgnoreCase(key)) {
                    return execute(new CopyOnWriteArrayList<>(cookies.get(key)))
                            .flatMap(m -> cache.getUser(m.getUid()))
                            .flatMap(content -> Mono.just(SpringUtil.getBean(UserBuilder.class).create(content)));
                }
            }
        }
        return Mono.empty();
    }

    /**
     * Execute Token Content
     *
     * @param list Http Cookie List Object
     * @return User Token Cache Model Object
     */
    public Mono<UserTokenCache.Model> execute(List<HttpCookie> list) {
        if (list == null || list.isEmpty()) {
            return Mono.empty();
        } else {
            final HttpCookie cookie = list.remove(0);
            return cache.getToken(cookie.getValue()).switchIfEmpty(execute(list));
        }
    }

}
