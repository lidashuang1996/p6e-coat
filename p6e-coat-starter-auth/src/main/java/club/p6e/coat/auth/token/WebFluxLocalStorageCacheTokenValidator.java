package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.cache.UserTokenCache;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author lidashuang
 * @version 1.0
 */
public class WebFluxLocalStorageCacheTokenValidator implements WebFluxTokenValidator {

    private final UserTokenCache cache;

    public WebFluxLocalStorageCacheTokenValidator(UserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<User> execute(ServerWebExchange context) {
        final ServerHttpRequest request = context.getRequest();
        final List<String> list = request.getHeaders().get("Authorization");
        if (list != null && !list.isEmpty()) {
            return execute(new CopyOnWriteArrayList<>(list))
                    .flatMap(m -> cache.getUser(m.getUid()))
                    .flatMap(user -> Mono.just(User.create(user)));
        }
        return Mono.empty();
    }

    public Mono<UserTokenCache.Token> execute(List<String> list) {
        if (list == null || list.isEmpty()) {
            return Mono.empty();
        } else {
            String token = null;
            while (!list.isEmpty()) {
                token = list.remove(0);
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring("Bearer ".length());
                    break;
                }
            }
            return token == null ? Mono.empty() : cache.getToken(token).switchIfEmpty(execute(list));
        }
    }

}
