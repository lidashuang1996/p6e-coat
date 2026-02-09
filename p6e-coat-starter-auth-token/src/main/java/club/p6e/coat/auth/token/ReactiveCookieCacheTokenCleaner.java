package club.p6e.coat.auth.token;

import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reactive Cookie Cache Token Cleaner
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveCookieCacheTokenCleaner implements ReactiveTokenCleaner {

    /**
     * Auth Cookie Name
     */
    protected static final String AUTH_COOKIE_NAME = "P6E_AUTH";

    /**
     * User Token Cache Object
     */
    protected final ReactiveUserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache User Token Cache Object
     */
    public ReactiveCookieCacheTokenCleaner(ReactiveUserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange) {
        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpResponse response = exchange.getResponse();
        final MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (!cookies.isEmpty()) {
            final List<HttpCookie> list = new ArrayList<>();
            for (final String key : cookies.keySet()) {
                if (AUTH_COOKIE_NAME.equalsIgnoreCase(key)) {
                    list.addAll(cookies.get(key));
                    response.addCookie(cookie(key, ""));
                }
            }
            return execute(list).switchIfEmpty(Mono.just("")).map(_ -> LocalDateTime.now());
        }
        return Mono.just(LocalDateTime.now());
    }

    /**
     * Execute Token Content
     *
     * @param list Http Cookie List Object
     * @return Token String Object
     */
    public Mono<String> execute(List<HttpCookie> list) {
        if (list == null || list.isEmpty()) {
            return Mono.empty();
        } else {
            final HttpCookie cookie = list.removeFirst();
            return cookie == null ? execute(list) : this.cache.getToken(cookie.getValue()).flatMap(m -> this.cache.cleanToken(m.getToken())).map(ReactiveUserTokenCache.Model::getToken).switchIfEmpty(execute(list));
        }
    }

    /**
     * Cookie
     *
     * @param name    Cookie Name
     * @param content Cookie Content
     * @return Response Cookie Object
     */
    public ResponseCookie cookie(String name, String content) {
        return ResponseCookie.from(name, content).path("/").maxAge(0).httpOnly(true).build();
    }

}
