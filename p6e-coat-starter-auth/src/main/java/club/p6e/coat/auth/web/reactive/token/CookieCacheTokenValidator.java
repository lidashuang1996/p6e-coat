package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.web.reactive.cache.UserTokenCache;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author lidashuang
 * @version 1.0
 */
public class CookieCacheTokenValidator implements TokenValidator {

    private final UserTokenCache cache;

    public CookieCacheTokenValidator(UserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<User> execute(ServerWebExchange context) {
        final ServerHttpRequest request = context.getRequest();
        final MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (!cookies.isEmpty()) {
            for (final String key : cookies.keySet()) {
                if ("P6e-User-Auth".equalsIgnoreCase(key)) {
                    return execute(new CopyOnWriteArrayList<>(cookies.get(key)))
                            .flatMap(m -> cache.getUser(m.getUid()))
                            .flatMap(user -> Mono.just(User.create(user)));
                }
            }
        }
        return Mono.empty();
    }

    public Mono<UserTokenCache.Token> execute(List<HttpCookie> list) {
        if (list == null || list.isEmpty()) {
            return Mono.empty();
        } else {
            final HttpCookie cookie = list.remove(0);
            return cache.getToken(cookie.getValue()).switchIfEmpty(execute(list));
        }
    }

}
