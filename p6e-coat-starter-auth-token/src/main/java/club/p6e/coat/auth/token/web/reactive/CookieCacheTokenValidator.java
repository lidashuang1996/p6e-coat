package club.p6e.coat.auth.token.web.reactive;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.common.utils.SpringUtil;
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
                    return execute(new ArrayList<>(cookies.get(key))).flatMap(content -> Mono.just(SpringUtil.getBean(UserBuilder.class).create(content)));
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
