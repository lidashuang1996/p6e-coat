package club.p6e.coat.auth.token.web.reactive;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Cookie Cache Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class CookieCacheTokenValidator implements TokenValidator {

    /**
     * Auth Cookie Name
     */
    protected static final String AUTH_COOKIE_NAME = "P6E_AUTH";
    /**
     * User Builder Object
     */
    protected final UserBuilder builder;

    /**
     * User Token Cache Object
     */
    protected final UserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param builder User Builder Object
     * @param cache   User Token Cache Object
     */
    public CookieCacheTokenValidator(UserBuilder builder, UserTokenCache cache) {
        this.builder = builder;
        this.cache = cache;
    }

    @Override
    public Mono<User> execute(ServerWebExchange context) {
        final ServerHttpRequest request = context.getRequest();
        final MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (!cookies.isEmpty()) {
            for (final String key : cookies.keySet()) {
                if (AUTH_COOKIE_NAME.equalsIgnoreCase(key)) {
                    return execute(new ArrayList<>(cookies.get(key))).flatMap(content -> Mono.just(builder.create(content)));
                }
            }
        }
        return Mono.empty();
    }

    /**
     * Execute Token Content
     *
     * @param list Http Cookie List Object
     * @return User String Object
     */
    public Mono<String> execute(List<HttpCookie> list) {
        if (list == null || list.isEmpty()) {
            return Mono.empty();
        } else {
            final HttpCookie cookie = list.remove(0);
            return cookie == null ? execute(list) : cache.getToken(cookie.getValue()).flatMap(m -> cache.getUser(m.getUid())).switchIfEmpty(execute(list));
        }
    }

}
